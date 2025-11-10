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
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtils {

    @Value("${security.jwt.key.secret}")
    private String privateKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    @Value("${security.jwt.expiration.time}")
    private long jwtExpirationTime;

    public String createToken(Authentication authentication) {
        log.debug("Creación de un token JWT para el usuario: {}", authentication.getPrincipal());

        try {
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

            String username = authentication.getPrincipal().toString();
            String authorities = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            Date issuedAt = new Date();
            Date expirationDate = new Date(issuedAt.getTime() + jwtExpirationTime);

            log.debug("Token emitido en: {}, caduca a las: {}", issuedAt, expirationDate);

            String jwtToken = JWT.create()
                    .withIssuer(this.userGenerator)
                    .withSubject(username)
                    .withClaim("authorities", authorities)
                    .withIssuedAt(issuedAt)
                    .withExpiresAt(expirationDate)
                    .sign(algorithm);

            log.info("Token JWT creado correctamente para el usuario: {}", username);
            return jwtToken;

        } catch (Exception e) {
            log.error("Error al crear el token JWT para el usuario: {}", authentication.getPrincipal(), e);
            throw new RuntimeException("Error al crear el token JWT", e);
        }
    }

    public DecodedJWT validateToken(String token) throws JWTVerificationException {
        log.debug("Validación del token JWT");

        try {
            if (token == null || token.trim().isEmpty()) {
                log.error("El token JWT es nulo o está vacío");
                throw new JWTVerificationException("El token no puede ser nulo ni vacío");
            }

            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build()
                    .verify(token);

            log.debug("Token JWT validado correctamente para el usuario: {}", decodedJWT.getSubject());
            return decodedJWT;

        } catch (TokenExpiredException e) {
            log.warn("El token JWT ha caducado: {}", e.getMessage());
            throw e;
        } catch (JWTVerificationException e) {
            log.error("Error en la validación del token JWT: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado durante la validación del token JWT", e);
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

    public Date getExpirationDate(DecodedJWT decodedJWT) {
        if (decodedJWT == null) {
            log.error("DecodedJWT es nulo");
            throw new IllegalArgumentException("DecodedJWT no puede ser null");
        }
        Date expiration = decodedJWT.getExpiresAt();
        log.debug("Fecha de caducidad del token: {}", expiration);
        return expiration;
    }

    public String getIssuer(DecodedJWT decodedJWT) {
        if (decodedJWT == null) {
            log.error("DecodedJWT es nulo");
            throw new IllegalArgumentException("DecodedJWT no puede ser null");
        }
        String issuer = decodedJWT.getIssuer();
        log.debug("Emisor de tokens: {}", issuer);
        return issuer;
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
}
