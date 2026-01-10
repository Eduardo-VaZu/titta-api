package com.titta.api.config.filter;

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
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
public class JwtTokenValidator extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    public JwtTokenValidator(JwtUtils jwtUtils, ObjectMapper objectMapper,
            TokenBlacklistRepository tokenBlacklistRepository) {
        this.jwtUtils = jwtUtils;
        this.objectMapper = objectMapper;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Si no hay token, CONTINUAMOS sin hacer nada.
        if (jwtToken == null || !jwtToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // Limpiamos el prefijo
        jwtToken = jwtToken.substring(7);

        try {
            // Validamos el token
            DecodedJWT decodedJWT = jwtUtils.validateAccessToken(jwtToken);

            // Chequeamos blacklist (Logout)
            if (tokenBlacklistRepository.existsById(jwtToken)) {
                sendErrorResponse(response, "Token no válido");
                return;
            }

            // Extraemos usuario y roles
            String username = jwtUtils.extractUserName(decodedJWT);
            String authorities = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();

            Collection<? extends GrantedAuthority> authoritiesList = AuthorityUtils
                    .commaSeparatedStringToAuthorityList(authorities);

            // Establecemos la autenticación en el contexto de Spring
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authoritiesList);
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            // Continuamos con la cadena de filtros
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            sendErrorResponse(response, "Token inválido o expirado: " + e.getMessage());
        }

    }

    private void sendErrorResponse(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                message,
                LocalDateTime.now());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}