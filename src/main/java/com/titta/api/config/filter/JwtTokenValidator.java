package com.titta.api.config.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.titta.api.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

// Esta clase extiende de OncePerRequestFilter para asegurar que se ejecuta UNA SOLA VEZ por cada petición.
public class JwtTokenValidator extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    // Le pasamos nuestra utilidad de JWT a través del constructor.
    public JwtTokenValidator(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. Extraemos el header "Authorization" de la petición.
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 2. Verificamos que el header exista y que comience con "Bearer ".
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extraemos el token quitando el prefijo "Bearer ".
            String token = authHeader.substring(7);

            try {
                // 3. Validamos el token usando nuestra clase JwtUtils.
                DecodedJWT decodedJWT = jwtUtils.validateToken(token);

                // 4. Extraemos el username y los roles del token ya validado.
                String username = jwtUtils.extractUserName(decodedJWT);
                String authoritiesString = decodedJWT.getClaim("authorities").asString();

                // 5. Creamos la lista de "authorities" que Spring Security entiende.
                Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesString);

                // 6. Creamos el objeto Authentication y lo guardamos en el contexto de seguridad.
                // Esto es lo que le dice a Spring "este usuario está autenticado para esta petición".
                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (JWTVerificationException e) {
                // Si la validación del token falla, limpiamos el contexto de seguridad.
                SecurityContextHolder.clearContext();
            }
        }

        // 7. Dejamos que la petición continúe su camino por el resto de los filtros.
        filterChain.doFilter(request, response);
    }
}