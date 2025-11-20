package com.titta.api.features.auth.service.impl;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.titta.api.config.exception.DuplicateResourceException;
import com.titta.api.config.util.JwtUtils;
import com.titta.api.domain.model.CredencialTradicional;
import com.titta.api.domain.model.Rol;
import com.titta.api.domain.model.TokenBlacklist;
import com.titta.api.domain.model.Usuario;
import com.titta.api.domain.model.enums.RolEnum;
import com.titta.api.domain.repository.RolRepository;
import com.titta.api.domain.repository.TokenBlacklistRepository;
import com.titta.api.domain.repository.UsuarioRepository;
import com.titta.api.features.auth.dto.request.AuthLoginRequest;
import com.titta.api.features.auth.dto.request.AuthRegisterRequest;
import com.titta.api.features.auth.dto.response.AuthLoginResponse;
import com.titta.api.features.auth.dto.response.AuthRegisterResponse;
import com.titta.api.features.auth.dto.response.RefreshTokenResponse;
import com.titta.api.features.auth.mapper.AuthMapper;
import com.titta.api.features.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TokenBlacklistRepository tokenBlacklistRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private AuthMapper authMapper;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${app.security.cookie.secure:false}")
    private boolean secureCookie;

    @Override
    public AuthRegisterResponse registerUser(AuthRegisterRequest registerRequest, HttpServletResponse response) {
        if (usuarioRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new DuplicateResourceException("El correo electrónico ya está registrado.");
        }

        Rol defaultRol = rolRepository.findByNombreRol(RolEnum.CLIENTE)
                .orElseThrow(() -> new RuntimeException("Error interno: El rol CLIENTE no se encuentra."));

        Usuario usuario = Usuario.builder()
                .nombre(registerRequest.nombre())
                .apellidoPaterno(registerRequest.apellidoPaterno())
                .apellidoMaterno(registerRequest.apellidoMaterno())
                .email(registerRequest.email())
                .estadoUsuario(true)
                .rol(defaultRol)
                .build();

        CredencialTradicional credencial = CredencialTradicional.builder()
                .usuario(usuario)
                .passwordHash(passwordEncoder.encode(registerRequest.password()))
                .build();

        usuario.setCredencialTradicional(credencial);

        usuarioRepository.save(usuario);

        return AuthRegisterResponse.builder()
                .message("Usuario registrado exitosamente")
                .status(true)
                .build();
    }

    @Override
    public AuthLoginResponse loginUser(AuthLoginRequest authLoginRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authLoginRequest.username(),
                        authLoginRequest.password())
        );

        Usuario usuarioClain = buildUsuarioClain(authLoginRequest.username());

        AuthLoginResponse.UsuarioResponseDto usuarioDto = authMapper.toUsuarioLoginResponseDto(usuarioClain);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = this.jwtUtils.createAccessToken(authentication, usuarioClain);
        String refreshToken = this.jwtUtils.createRefreshToken(authentication);

        response.addCookie(createRefreshTokenCookie(refreshToken));

        return AuthLoginResponse.builder()
                .usuario(usuarioDto)
                .jwt(accessToken)
                .message("Usuario logueado exitosamente")
                .status(true)
                .build();
    }

    @Override
    public RefreshTokenResponse refreshAccessToken(String refreshToken, HttpServletResponse response) {
        try {
            DecodedJWT decodedJWT = jwtUtils.validateRefreshToken(refreshToken);

            log.debug("Claims del refresh token: {}", jwtUtils.returnAllClaims(decodedJWT));

            String username = jwtUtils.extractUserName(decodedJWT);
            String jti = jwtUtils.extractJti(decodedJWT);

            if (tokenBlacklistRepository.existsById(jti)) {
                log.warn("Intento de refresco con token en blacklist (JTI: {})", jti);
                throw new BadCredentialsException("Token inválido o expirado");
            }

            Usuario usuarioClain = buildUsuarioClain(username);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(), null, userDetails.getAuthorities()
            );

            String newAccessToken = jwtUtils.createAccessToken(authentication, usuarioClain);
            String newRefreshToken = jwtUtils.createRefreshToken(authentication);

            response.addCookie(createRefreshTokenCookie(newRefreshToken));

            return RefreshTokenResponse.builder()
                    .jwt(newAccessToken)
                    .message("Tokens refrescados exitosamente")
                    .build();
        } catch (Exception e) {
            log.error("Error al refrescar el token: {}", e.getMessage());
            throw new BadCredentialsException("Refresh token inválido o expirado", e);
        }
    }

    @Override
    public void logoutUser(String refreshToken, String authorizationHeader) {
        if (refreshToken != null && !refreshToken.trim().isEmpty()) {
            try {
                DecodedJWT decodedJWT = jwtUtils.validateRefreshToken(refreshToken);
                String jti = jwtUtils.extractJti(decodedJWT);

                LocalDateTime expiracion = decodedJWT.getExpiresAt().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                TokenBlacklist tokenBlacklist = TokenBlacklist.builder()
                        .jti(jti)
                        .fechaExpiracion(expiracion)
                        .build();

                tokenBlacklistRepository.save(tokenBlacklist);
                log.info("Refresh Token (JTI: {}) añadido a la blacklist (logout).", jti);

            } catch (Exception e) {
                log.warn("Intento de logout con refresh token inválido: {}", e.getMessage());
            }
        }

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String accessToken = authorizationHeader.substring(7);
                DecodedJWT decodedJWT = jwtUtils.validateAccessToken(accessToken);
                String jti = decodedJWT.getId();

                LocalDateTime expiracion = decodedJWT.getExpiresAt().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                TokenBlacklist tokenBlacklist = TokenBlacklist.builder()
                        .jti(jti)
                        .fechaExpiracion(expiracion)
                        .build();

                tokenBlacklistRepository.save(tokenBlacklist);
                log.info("Access Token (JTI: {}) añadido a la blacklist (logout).", jti);

            } catch (TokenExpiredException e) {
                log.debug("Access token ya está expirado, no se añade a blacklist.");
            } catch (Exception e) {
                log.warn("Intento de logout con access token inválido: {}", e.getMessage());
            }
        }
    }

    private Cookie createRefreshTokenCookie(String token) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", token);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/api/v1/auth");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        refreshTokenCookie.setSecure(secureCookie);
        return refreshTokenCookie;
    }

    private Usuario buildUsuarioClain(String username) {

        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe."));

        return Usuario.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .apellidoPaterno(usuario.getApellidoPaterno())
                .apellidoMaterno(usuario.getApellidoMaterno())
                .email(usuario.getEmail())
                .build();
    }
}