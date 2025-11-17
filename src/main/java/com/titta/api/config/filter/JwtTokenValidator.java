package com.titta.api.config.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.titta.api.config.exception.error.ErrorResponse;
import com.titta.api.config.util.JwtUtils;
import com.titta.api.domain.repository.TokenBlacklistRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

@Slf4j
public class JwtTokenValidator extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    public JwtTokenValidator(JwtUtils jwtUtils, ObjectMapper objectMapper, TokenBlacklistRepository tokenBlacklistRepository) {
        this.jwtUtils = jwtUtils;
        this.objectMapper = objectMapper;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
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

                DecodedJWT decodedJWT = jwtUtils.validateAccessToken(jwtToken);

                String jti = decodedJWT.getId();
                if (jti == null || tokenBlacklistRepository.existsById(jti)) {
                    log.warn("Token JWT está en la blacklist (JTI: {})", jti);
                    handleAuthenticationError(response, "Token no válido", HttpStatus.UNAUTHORIZED);
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

                Authentication authenticationToken = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);

                log.info("Autenticación JWT exitosa para el usuario: {}", username);
                SecurityContext context = SecurityContextHolder.getContext();
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
                handleAuthenticationError(response, "Token inválido", HttpStatus.UNAUTHORIZED); // ✅ 401
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

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                message,
                LocalDateTime.now()
        );

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}