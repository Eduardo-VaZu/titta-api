package com.titta.api.features.auth.service.impl;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.titta.api.config.exception.DuplicateResourceException;
import com.titta.api.config.security.jwt.JwtUtils;
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
import com.titta.api.features.auth.dto.result.AuthLoginResult;
import com.titta.api.features.auth.dto.result.AuthRefreshResult;
import com.titta.api.features.auth.mapper.AuthMapper;
import com.titta.api.features.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final RolRepository rolRepository;
    private final AuthMapper authMapper;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthRegisterResponse registerUser(AuthRegisterRequest registerRequest) {
        if (usuarioRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new DuplicateResourceException("El correo electronico ya esta registrado.");
        }

        Rol defaultRol = rolRepository.findByNombreRol(RolEnum.CLIENTE)
                .orElseThrow(() -> new RuntimeException("Error interno: el rol CLIENTE no se encuentra."));

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
    public AuthLoginResult loginUser(AuthLoginRequest authLoginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authLoginRequest.username(), authLoginRequest.password()));

        Usuario usuarioClaim = buildUsuarioClaim(authLoginRequest.username());
        AuthLoginResponse.UsuarioResponseDto usuarioDto = authMapper.toUsuarioLoginResponseDto(usuarioClaim);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createAccessToken(authentication, usuarioClaim);
        String refreshToken = jwtUtils.createRefreshToken(authentication);

        AuthLoginResponse loginResponse = AuthLoginResponse.builder()
                .usuario(usuarioDto)
                .jwt(accessToken)
                .message("Usuario logueado exitosamente")
                .status(true)
                .build();

        return AuthLoginResult.builder()
                .response(loginResponse)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public AuthRefreshResult refreshAccessToken(String refreshToken) {
        try {
            DecodedJWT decodedJWT = jwtUtils.validateRefreshToken(refreshToken);

            String username = jwtUtils.extractUserName(decodedJWT);
            String jti = jwtUtils.extractJti(decodedJWT);
            if (jti == null || jti.isBlank()) {
                throw new BadCredentialsException("Refresh token invalido");
            }

            if (tokenBlacklistRepository.existsById(jti)) {
                log.warn("Intento de refresh con token ya invalidado. JTI={}", jti);
                throw new BadCredentialsException("Refresh token invalido o expirado");
            }

            // Rotation one-time-use: se invalida el refresh token usado antes de emitir uno nuevo.
            blacklistToken(jti, toLocalDateTime(decodedJWT));

            Usuario usuarioClaim = buildUsuarioClaim(username);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(), null, userDetails.getAuthorities());

            String newAccessToken = jwtUtils.createAccessToken(authentication, usuarioClaim);
            String newRefreshToken = jwtUtils.createRefreshToken(authentication);

            RefreshTokenResponse responsePayload = RefreshTokenResponse.builder()
                    .jwt(newAccessToken)
                    .message("Tokens refrescados exitosamente")
                    .build();

            return AuthRefreshResult.builder()
                    .response(responsePayload)
                    .refreshToken(newRefreshToken)
                    .build();
        } catch (Exception e) {
            log.error("Error al refrescar token", e);
            throw new BadCredentialsException("Refresh token invalido o expirado", e);
        }
    }

    @Override
    @Transactional
    public void logoutUser(String refreshToken, String authorizationHeader) {
        if (refreshToken != null && !refreshToken.trim().isEmpty()) {
            try {
                DecodedJWT decodedJWT = jwtUtils.validateRefreshToken(refreshToken);
                String jti = jwtUtils.extractJti(decodedJWT);

                if (jti != null && !jti.isBlank()) {
                    blacklistToken(jti, toLocalDateTime(decodedJWT));
                    log.info("Refresh token invalidado en logout. JTI={}", jti);
                }
            } catch (Exception e) {
                log.warn("Intento de logout con refresh token invalido: {}", e.getMessage());
            }
        }

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String accessToken = authorizationHeader.substring(7);
                DecodedJWT decodedJWT = jwtUtils.validateAccessToken(accessToken);
                String jti = decodedJWT.getId();

                if (jti != null && !jti.isBlank()) {
                    blacklistToken(jti, toLocalDateTime(decodedJWT));
                    log.info("Access token invalidado en logout. JTI={}", jti);
                }
            } catch (TokenExpiredException e) {
                log.debug("Access token ya expirado, no se agrega a blacklist.");
            } catch (Exception e) {
                log.warn("Intento de logout con access token invalido: {}", e.getMessage());
            }
        }
    }

    private void blacklistToken(String jti, LocalDateTime expiracion) {
        TokenBlacklist tokenBlacklist = TokenBlacklist.builder()
                .jti(jti)
                .fechaExpiracion(expiracion)
                .build();
        tokenBlacklistRepository.save(tokenBlacklist);
    }

    private LocalDateTime toLocalDateTime(DecodedJWT decodedJWT) {
        return decodedJWT.getExpiresAt().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private Usuario buildUsuarioClaim(String username) {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe."));

        return Usuario.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .apellidoPaterno(usuario.getApellidoPaterno())
                .apellidoMaterno(usuario.getApellidoMaterno())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .estadoUsuario(usuario.isEstadoUsuario())
                .build();
    }
}
