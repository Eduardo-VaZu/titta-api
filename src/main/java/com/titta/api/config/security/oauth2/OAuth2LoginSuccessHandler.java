package com.titta.api.config.security.oauth2;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import com.titta.api.config.security.jwt.JwtUtils;
import com.titta.api.domain.model.Usuario;
import com.titta.api.domain.repository.UsuarioRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

        private final JwtUtils jwtUtils;
        private final UsuarioRepository usuarioRepository;

        @Value("${app.oauth2.redirect-uri:http://localhost:4200/login}")
        private String redirectUri;

        @Override
        @Transactional
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                        Authentication authentication) throws IOException, ServletException {

                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String email = oauthToken.getPrincipal().getAttribute("email");

                log.info("Procesando login OAuth2 exitoso para el email: {}", email);

                Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

                if (usuarioOpt.isPresent()) {
                        Usuario usuario = usuarioOpt.get();

                        List<SimpleGrantedAuthority> authorities = usuario.getRol().getPermisos().stream()
                                        .map(permiso -> new SimpleGrantedAuthority(permiso.getNombre()))
                                        .collect(Collectors.toList());

                        authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombreRol().name()));

                        Authentication appAuth = new UsernamePasswordAuthenticationToken(
                                        usuario.getEmail(),
                                        null,
                                        authorities);

                        String accessToken = jwtUtils.createAccessToken(appAuth, usuario);

                        String refreshToken = jwtUtils.createRefreshToken(appAuth);

                        log.info("Tokens generados correctamente para usuario: {}", email);

                        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                                        .queryParam("token", accessToken)
                                        .queryParam("refresh_token", refreshToken)
                                        .build().toUriString();

                        getRedirectStrategy().sendRedirect(request, response, targetUrl);
                } else {
                        log.warn("El email {} no existe en la base de datos local.", email);

                        String errorUrl = UriComponentsBuilder.fromUriString(redirectUri)
                                        .queryParam("error", "usuario_no_encontrado")
                                        .queryParam("email", email)
                                        .build().toUriString();

                        getRedirectStrategy().sendRedirect(request, response, errorUrl);
                }
        }
}
