package com.titta.api.features.auth.service.impl;

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
import com.titta.api.features.auth.mapper.AuthMapper;
import com.titta.api.features.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.HashMap;
import java.util.Map;

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
//
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//                usuarioCreado.getEmail(), null,
//                Arrays.asList(new SimpleGrantedAuthority("ROLE_".concat(usuarioCreado.getRol().getNombreRol().name())))
//        );
//
//        String accessToken = this.jwtUtils.createAccessToken(authentication);
//        String refreshToken = this.jwtUtils.createRefreshToken(authentication);
//
//        response.addCookie(createRefreshTokenCookie(refreshToken));

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

//        Usuario usuario = usuarioRepository.findByEmail(authLoginRequest.username())
//                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + authLoginRequest.username() + " no existe."));

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
    public Map<String, String> refreshAccessToken(String refreshToken) {
        try {
            DecodedJWT decodedJWT = jwtUtils.validateRefreshToken(refreshToken);
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

            Map<String, String> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("message", "Token de acceso refrescado exitosamente");
            return response;

        } catch (Exception e) {
            log.error("Error al refrescar el token: {}", e.getMessage());
            throw new BadCredentialsException("Refresh token inválido o expirado", e);
        }
    }

    @Override
    public void logoutUser(String refreshToken) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return;
        }

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
            log.info("Token (JTI: {}) añadido a la blacklist (logout).", jti);

        } catch (Exception e) {
            log.warn("Intento de logout con token inválido o expirado: {}", e.getMessage());
        }
    }

    private Cookie createRefreshTokenCookie(String token) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", token);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/api/v1/auth");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        // refreshTokenCookie.setSecure(true); // Deberías habilitar esto en producción (con HTTPS)
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