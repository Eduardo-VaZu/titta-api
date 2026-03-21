package com.titta.api.config.security.oauth2;

import com.titta.api.config.security.jwt.JwtUtils;
import com.titta.api.domain.model.Usuario;
import com.titta.api.domain.repository.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String REFRESH_COOKIE_NAME = "refresh_token";
    private static final String REFRESH_COOKIE_PATH = "/api/v1/auth";
    private static final long REFRESH_COOKIE_SECONDS = 7L * 24 * 60 * 60;

    private final JwtUtils jwtUtils;
    private final UsuarioRepository usuarioRepository;

    @Value("${app.oauth2.redirect-uri:http://localhost:4200/login}")
    private String redirectUri;

    @Value("${app.security.cookie.secure:false}")
    private boolean secureCookie;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String email = oauthToken.getPrincipal().getAttribute("email");

        log.info("Procesando login OAuth2 exitoso para email={}", email);

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            log.warn("Login OAuth2 rechazado. El email no existe en la base local.");
            String errorUrl = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("error", "usuario_no_encontrado")
                    .build()
                    .toUriString();
            getRedirectStrategy().sendRedirect(request, response, errorUrl);
            return;
        }

        Usuario usuario = usuarioOpt.get();
        List<SimpleGrantedAuthority> authorities = usuario.getRol().getPermisos().stream()
                .map(permiso -> new SimpleGrantedAuthority(permiso.getNombre()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombreRol().name()));

        Authentication appAuth = new UsernamePasswordAuthenticationToken(usuario.getEmail(), null, authorities);
        String refreshToken = jwtUtils.createRefreshToken(appAuth);

        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Lax")
                .path(REFRESH_COOKIE_PATH)
                .maxAge(REFRESH_COOKIE_SECONDS)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("oauth2", "success")
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
