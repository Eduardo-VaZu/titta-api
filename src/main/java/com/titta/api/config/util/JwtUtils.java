package com.titta.api.config.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtils {

    @Value("${security.jwt.key.secret}")
    private String privateKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    @Value("${security.jwt.expiration.access-token}")
    private long jwtAccessExpirationTime;

    @Value("${security.jwt.expiration.refresh-token}")
    private long jwtRefreshExpirationTime;

    public String createAccessToken(Authentication authentication) {
        log.debug("Creación de un Access Token para el usuario: {}", authentication.getPrincipal());

        try {
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

            String username = authentication.getPrincipal().toString();
            String authorities = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            Date issuedAt = new Date();
            Date expirationDate = new Date(issuedAt.getTime() + jwtAccessExpirationTime);

            log.debug("Access Token emitido en: {}, caduca a las: {}", issuedAt, expirationDate);

            String jwtToken = JWT.create()
                    .withIssuer(this.userGenerator)
                    .withSubject(username)
                    .withClaim("authorities", authorities)
                    .withIssuedAt(issuedAt)
                    .withExpiresAt(expirationDate)
                    .sign(algorithm);

            log.info("Access Token creado correctamente para el usuario: {}", username);
            return jwtToken;

        } catch (Exception e) {
            log.error("Error al crear el Access Token para el usuario: {}", authentication.getPrincipal(), e);
            throw new RuntimeException("Error al crear el Access Token", e);
        }
    }

    public String createRefreshToken(Authentication authentication) {
        log.debug("Creación de un Refresh Token para el usuario: {}", authentication.getPrincipal());

        try {
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);
            String username = authentication.getPrincipal().toString();
            Date issuedAt = new Date();

            Date expirationDate = new Date(issuedAt.getTime() + jwtRefreshExpirationTime);

            String jwtToken = JWT.create()
                    .withIssuer(this.userGenerator)
                    .withSubject(username)
                    .withIssuedAt(issuedAt)
                    .withExpiresAt(expirationDate)
                    .withJWTId(UUID.randomUUID().toString())
                    .sign(algorithm);

            log.info("Refresh Token creado correctamente para el usuario: {}", username);
            return jwtToken;

        } catch (Exception e) {
            log.error("Error al crear el Refresh Token para el usuario: {}", authentication.getPrincipal(), e);
            throw new RuntimeException("Error al crear el Refresh Token", e);
        }
    }

    public DecodedJWT validateAccessToken(String token) throws JWTVerificationException {
        log.debug("Validación del Access Token");

        try {
            if (token == null || token.trim().isEmpty()) {
                log.error("El Access Token es nulo o está vacío");
                throw new JWTVerificationException("El token no puede ser nulo ni vacío");
            }

            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build()
                    .verify(token);

            log.debug("Access Token validado correctamente para el usuario: {}", decodedJWT.getSubject());
            return decodedJWT;

        } catch (TokenExpiredException e) {
            log.warn("El Access Token ha caducado: {}", e.getMessage());
            throw e;
        } catch (JWTVerificationException e) {
            log.error("Error en la validación del Access Token: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado durante la validación del Access Token", e);
            throw new JWTVerificationException("Error en la validación del token", e);
        }
    }

    public DecodedJWT validateRefreshToken(String token) throws JWTVerificationException {
        log.debug("Validación del Refresh Token");

        try {
            if (token == null || token.trim().isEmpty()) {
                log.error("El Refresh Token es nulo o está vacío");
                throw new JWTVerificationException("El token no puede ser nulo ni vacío");
            }

            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build()
                    .verify(token);

            log.debug("Refresh Token validado correctamente para el usuario: {}", decodedJWT.getSubject());
            return decodedJWT;

        } catch (TokenExpiredException e) {
            log.warn("El Refresh Token ha caducado: {}", e.getMessage());
            throw e;
        } catch (JWTVerificationException e) {
            log.error("Error en la validación del Refresh Token: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado durante la validación del Refresh Token", e);
            throw new JWTVerificationException("Error en la validación del token", e);
        }
    }

    public String extractUserName(DecodedJWT decodedJWT) {
        if (decodedJWT == null) {
            log.error("DecodedJWT es nulo");
            throw new IllegalArgumentException("DecodedJWT no puede ser null");
        }
        String username = decodedJWT.getSubject();
        log.debug("Nombre de usuario extraído de JWT: {}", username);
        return username;
    }

    public boolean isTokenExpired(DecodedJWT decodedJWT) {
        if (decodedJWT == null) {
            log.error("DecodedJWT es nulo");
            return true;
        }
        Date expiration = decodedJWT.getExpiresAt();
        boolean isExpired = expiration != null && expiration.before(new Date());
        log.debug("Comprobación de caducidad del token - caduca a las: {}, está caducado: {}", expiration, isExpired);
        return isExpired;
    }

    public boolean validateTokenStructure(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.error("El token es nulo o está vacío");
            return false;
        }

        String[] parts = token.split("\\.");
        boolean isValid = parts.length == 3;
        log.debug("Validación de la estructura del token - partes: {}, válido: {}", parts.length, isValid);
        return isValid;
    }

    public String extractJti(DecodedJWT decodedJWT) {
        if (decodedJWT == null) {
            log.error("DecodedJWT es nulo");
            throw new IllegalArgumentException("DecodedJWT no puede ser null");
        }
        String jti = decodedJWT.getId();
        log.debug("JTI extraído de JWT: {}", jti);
        return jti;
    }
}