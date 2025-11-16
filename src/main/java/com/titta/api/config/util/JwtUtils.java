package com.titta.api.config.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.titta.api.domain.model.Usuario;
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

    private final Algorithm algorithm;
    private final JWTVerifier accessTokenVerifier;
    private final JWTVerifier refreshTokenVerifier;

    public JwtUtils(@Value("${security.jwt.key.secret}") String privateKey,
                    @Value("${security.jwt.user.generator}") String userGenerator) {
        if (privateKey == null || privateKey.trim().isEmpty()) {
            log.error("La clave secreta de JWT no está configurada (security.jwt.key.secret)");
            throw new IllegalArgumentException("La clave secreta de JWT no puede ser nula o vacía");
        }
        if (userGenerator == null || userGenerator.trim().isEmpty()) {
            log.error("El generador de usuarios JWT no está configurado (security.jwt.user.generator)");
            throw new IllegalArgumentException("El generador de usuarios JWT no puede ser nulo o vacío");
        }

        this.privateKey = privateKey;
        this.userGenerator = userGenerator;

        log.debug("Inicializando el algoritmo HMAC256 para JWT...");
        this.algorithm = Algorithm.HMAC256(privateKey);

        log.debug("Creando verificadores de Access Token y Refresh Token...");
        this.accessTokenVerifier = JWT.require(this.algorithm)
                .withIssuer(this.userGenerator)
                .build();

        this.refreshTokenVerifier = JWT.require(this.algorithm)
                .withIssuer(this.userGenerator)
                .build();
    }

    public String createAccessToken(Authentication authentication, Usuario usuario) {
        log.debug("Creación de un Access Token para el usuario: {}", authentication.getPrincipal());

        try {
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
                    .withClaim("IdUsuario", usuario.getIdUsuario())
                    .withClaim("Nombre", usuario.getNombre())
                    .withClaim("ApellidoPaterno", usuario.getApellidoPaterno())
                    .withClaim("ApellidoMaterno", usuario.getApellidoMaterno())
                    .withIssuedAt(issuedAt)
                    .withExpiresAt(expirationDate)
                    .sign(this.algorithm);

            log.info("Access Token creado correctamente para el usuario: {}", username);
            return jwtToken;

        } catch (Exception e) {
            log.error("Error al crear el Access Token para el usuario: {}", authentication.getPrincipal(), e);
            throw new RuntimeException("Error al crear el Access Token", e);
        }
    }

    public DecodedJWT validateAccessToken(String token) throws JWTVerificationException {
        log.debug("Validación del Access Token");

        try {
            if (token == null || token.trim().isEmpty()) {
                log.error("El Access Token es nulo o está vacío");
                throw new JWTVerificationException("El token no puede ser nulo ni vacío");
            }

            DecodedJWT decodedJWT = this.accessTokenVerifier.verify(token);
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

    public String createRefreshToken(Authentication authentication) {
        log.debug("Creación de un Refresh Token para el usuario: {}", authentication.getPrincipal());

        try {
            String username = authentication.getPrincipal().toString();
            Date issuedAt = new Date();
            Date expirationDate = new Date(issuedAt.getTime() + jwtRefreshExpirationTime);

            String jwtToken = JWT.create()
                    .withIssuer(this.userGenerator)
                    .withSubject(username)
                    .withIssuedAt(issuedAt)
                    .withExpiresAt(expirationDate)
                    .withJWTId(UUID.randomUUID().toString())
                    .sign(this.algorithm);

            log.info("Refresh Token creado correctamente para el usuario: {}", username);
            return jwtToken;

        } catch (Exception e) {
            log.error("Error al crear el Refresh Token para el usuario: {}", authentication.getPrincipal(), e);
            throw new RuntimeException("Error al crear el Refresh Token", e);
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

            DecodedJWT decodedJWT = this.refreshTokenVerifier.verify(token);
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