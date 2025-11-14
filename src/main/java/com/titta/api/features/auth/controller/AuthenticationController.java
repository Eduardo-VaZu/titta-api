package com.titta.api.features.auth.controller;

import com.titta.api.config.exception.error.ErrorResponse;
import com.titta.api.features.auth.dto.request.AuthLoginRequest;
import com.titta.api.features.auth.dto.request.AuthRegisterRequest;
import com.titta.api.features.auth.dto.response.AuthResponse;
import com.titta.api.features.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Endpoints para el registro, inicio de sesión y gestión de tokens.")
public class AuthenticationController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Inicio de sesión (Log In)",
            description = "Autentica un usuario con email (username) y contraseña. " +
                    "Devuelve el 'accessToken' (jwt) en el body y el 'refreshToken' en una cookie HttpOnly.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej. falta email o password)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas (usuario o contraseña incorrectos)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/log-in")
    public ResponseEntity<AuthResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciales del usuario para iniciar sesión",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuthLoginRequest.class)))
            @RequestBody @Valid AuthLoginRequest userRequest,
            HttpServletResponse response) {

        AuthResponse authResponse = this.authService.loginUser(userRequest, response);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @Operation(summary = "Registro de nuevo usuario (Sign Up)",
            description = "Crea un nuevo usuario en el sistema. " +
                    "Devuelve el 'accessToken' (jwt) en el body y el 'refreshToken' en una cookie HttpOnly.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej. contraseña no cumple formato)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto (ej. el correo ya está registrado)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo usuario a registrar",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuthRegisterRequest.class)))
            @RequestBody @Valid AuthRegisterRequest registerRequest,
            HttpServletResponse response) {

        AuthResponse authResponse = authService.registerUser(registerRequest, response);
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "Refrescar el token de acceso",
            description = "Usa el 'refresh_token' (enviado automáticamente como cookie HttpOnly) para obtener un nuevo token de acceso (jwt).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token de acceso refrescado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"accessToken\": \"new_jwt_token...\", \"message\": \"...\"}"))),
            @ApiResponse(responseCode = "401", description = "Refresh token no encontrado, inválido o expirado (Cookie 'refresh_token' ausente o en blacklist)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(
            @Parameter(in = ParameterIn.COOKIE,
                    name = "refresh_token",
                    description = "Token de refresco (generalmente enviado automáticamente por el navegador)",
                    required = true)
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {

        if (refreshToken == null) {
            return new ResponseEntity<>(Map.of("error", "Refresh token no encontrado"), HttpStatus.UNAUTHORIZED);
        }
        Map<String, String> response = authService.refreshAccessToken(refreshToken);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Cerrar sesión (Logout)",
            description = "Invalida el 'refresh_token' (añadiéndolo a la blacklist del servidor) y limpia la cookie HttpOnly en el navegador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout exitoso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Logout exitoso\"}")))
    })
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @Parameter(in = ParameterIn.COOKIE,
                    name = "refresh_token",
                    description = "Token de refresco (generalmente enviado automáticamente por el navegador).",
                    required = false)
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {

        authService.logoutUser(refreshToken);

        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/api/v1/auth");
        // cookie.setSecure(true);

        response.addCookie(cookie);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Logout exitoso");

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
