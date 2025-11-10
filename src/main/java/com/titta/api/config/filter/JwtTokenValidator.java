package com.titta.api.config.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.titta.api.config.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

@Slf4j
public class JwtTokenValidator extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtTokenValidator(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.debug("Procesamiento de la solicitud a: {}", request.getRequestURI());

        if (jwtToken != null) {
            log.debug("Encabezado de autorización encontrado");

            if (!jwtToken.startsWith("Bearer ")) {
                log.warn("Formato de encabezado de autorización no válido. Prefijo 'Bearer' esperado");
                handleAuthenticationError(response, "Formato de encabezado de autorización no válido", HttpStatus.UNAUTHORIZED);
                return;
            }

            try {
                jwtToken = jwtToken.substring(7);
                log.debug("Token JWT extraído");

                if (!jwtUtils.validateTokenStructure(jwtToken)) {
                    log.warn("Estructura de token JWT no válida");
                    handleAuthenticationError(response, "Estructura de token no válida", HttpStatus.UNAUTHORIZED);
                    return;
                }

                DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);

                if (jwtUtils.isTokenExpired(decodedJWT)) {
                    log.warn("El token JWT ha caducado para el usuario: {}", jwtUtils.extractUserName(decodedJWT));
                    handleAuthenticationError(response, "El token ha caducado", HttpStatus.UNAUTHORIZED);
                    return;
                }

                String username = jwtUtils.extractUserName(decodedJWT);
                String stringAuthorities = decodedJWT.getClaim("authorities").asString();

                if (stringAuthorities == null || stringAuthorities.trim().isEmpty()) {
                    log.warn("No se encontraron autoridades en el token JWT para el usuario: {}", username);
                    handleAuthenticationError(response, "No se encontraron autoridades en el token", HttpStatus.UNAUTHORIZED);
                    return;
                }

                Collection<? extends GrantedAuthority> authorities = Arrays.stream(stringAuthorities.split(","))
                        .filter(auth -> auth != null && !auth.trim().isEmpty())
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                if (authorities.isEmpty()) {
                    log.warn("Lista de autoridades vacía para el usuario: {}", username);
                    handleAuthenticationError(response, "No se han encontrado autoridades válidas", HttpStatus.UNAUTHORIZED);
                    return;
                }

                log.info("Autenticación JWT exitosa para el usuario: {}", username);
                SecurityContext context = SecurityContextHolder.getContext();
                Authentication authenticationToken = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);
                context.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(context);

            } catch (TokenExpiredException e) {
                log.warn("Token JWT caducado: {}", e.getMessage());
                handleAuthenticationError(response, "El token ha caducado", HttpStatus.UNAUTHORIZED);
                return;
            } catch (JWTVerificationException e) {
                log.warn("Error en la verificación de JWT: {}", e.getMessage());
                handleAuthenticationError(response, "Token no válido", HttpStatus.UNAUTHORIZED);
                return;
            } catch (Exception e) {
                log.error("Error inesperado durante la validación de JWT", e);
                handleAuthenticationError(response, "Error de autenticación", HttpStatus.INTERNAL_SERVER_ERROR);
                return;
            }
        } else {
            log.debug("No se encontró ningún encabezado de autorización para la solicitud: {}", request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }

    private void handleAuthenticationError(HttpServletResponse response, String message, HttpStatus status)
            throws IOException {
        log.error("Error de autenticación: {} - Status: {}", message, status.value());
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\":\"%s\",\"status\":%d}", message, status.value()));
    }
}