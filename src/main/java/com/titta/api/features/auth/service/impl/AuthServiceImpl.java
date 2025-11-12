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
import com.titta.api.features.auth.dto.response.AuthResponse;
import com.titta.api.features.auth.mapper.AuthMapper;
import com.titta.api.features.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
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


    @Override
    public AuthResponse registerUser(AuthRegisterRequest registerRequest, HttpServletResponse response) {
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
        Usuario usuarioCreado = usuarioRepository.save(usuario);

        AuthResponse.UsuarioResponseDto usuarioDto = authMapper.toUsuarioResponseDto(usuarioCreado);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                usuarioCreado.getEmail(), null,
                Arrays.asList(new SimpleGrantedAuthority("ROLE_".concat(usuarioCreado.getRol().getNombreRol().name())))
        );

        String accessToken = this.jwtUtils.createAccessToken(authentication);
        String refreshToken = this.jwtUtils.createRefreshToken(authentication);

        response.addCookie(createRefreshTokenCookie(refreshToken));

        return new AuthResponse(usuarioDto, accessToken, "Usuario registrado y logueado exitosamente", true);
    }

    @Override
    public AuthResponse loginUser(AuthLoginRequest authLoginRequest, HttpServletResponse response) {
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = this.jwtUtils.createAccessToken(authentication);
        String refreshToken = this.jwtUtils.createRefreshToken(authentication);

        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe."));

        AuthResponse.UsuarioResponseDto usuarioDto = authMapper.toUsuarioResponseDto(usuario);

        response.addCookie(createRefreshTokenCookie(refreshToken));

        return new AuthResponse(usuarioDto, accessToken, "Usuario logueado exitosamente", true);
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

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(), null, userDetails.getAuthorities()
            );

            String newAccessToken = jwtUtils.createAccessToken(authentication);

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

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Usuario no encontrado.");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Contraseña inválida.");
        }

        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }

    private Cookie createRefreshTokenCookie(String token) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", token);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/api/v1/auth");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        // refreshTokenCookie.setSecure(true);
        return refreshTokenCookie;
    }
}