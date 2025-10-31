package com.titta.api.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Component // Le decimos a Spring que esta clase es un componente y que la gestione.
public class JwtUtils {

    // Inyectamos la clave secreta desde application.properties. Es la firma de nuestros tokens.
    @Value("${security.jwt.key.secret}")
    private String privateKey;

    // Inyectamos el nombre del generador del token, también desde application.properties.
    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    /**
     * MÉTODO PARA CREAR EL TOKEN JWT
     * Recibe el objeto Authentication que Spring nos da cuando un usuario se loguea con éxito.
     */
    public String createToken(Authentication authentication) {
        // 1. Definimos el algoritmo de encriptación. Usaremos HMAC256, que necesita nuestra clave secreta.
        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

        // 2. Obtenemos el "principal" (el usuario) y sus "authorities" (roles/permisos).
        String username = authentication.getPrincipal().toString();
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority) // Mapeamos cada rol a su nombre en String.
                .collect(Collectors.joining(",")); // Los unimos en un solo String separado por comas. Ej: "ROLE_ADMIN,ROLE_USER"

        // 3. Creamos el token con sus claims (la "información" que lleva dentro).
        String jwtToken = JWT.create()
                .withIssuer(this.userGenerator) // Quién emite el token (nosotros).
                .withSubject(username) // A quién le pertenece el token (el usuario).
                .withClaim("authorities", authorities) // Agregamos un claim personalizado con los roles.
                .withIssuedAt(new Date()) // Fecha de emisión del token.
                .withExpiresAt(new Date(System.currentTimeMillis() + 1800000)) // Fecha de expiración (30 minutos).
                .sign(algorithm); // Finalmente, lo firmamos con el algoritmo.

        return jwtToken;
    }

    /**
     * MÉTODO PARA VALIDAR EL TOKEN JWT
     * Recibe el token que nos envía el cliente en la cabecera de la petición.
     */
    public DecodedJWT validateToken(String token) {
        try {
            // 1. Creamos el mismo algoritmo que usamos para firmar.
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

            // 2. Creamos un verificador con ese algoritmo y le decimos que espere el mismo "issuer".
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build();

            // 3. Verificamos el token. Si la firma es inválida o el token ha expirado, lanzará una excepción.
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT;

        } catch (JWTVerificationException exception) {
            // Si la validación falla, lanzamos una excepción clara.
            throw new JWTVerificationException("Token inválido, no autorizado");
        }
    }

    /**
     * MÉTODO PARA EXTRAER EL USERNAME DEL TOKEN
     * El "subject" del token es el username.
     */
    public String extractUserName(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }
}
