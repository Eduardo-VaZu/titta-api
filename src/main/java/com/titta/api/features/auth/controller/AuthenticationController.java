package com.titta.api.features.auth.controller;

import com.titta.api.features.auth.dto.request.AuthLoginRequest;
import com.titta.api.features.auth.dto.request.AuthRegisterRequest;
import com.titta.api.features.auth.dto.response.AuthLoginResponse;
import com.titta.api.features.auth.dto.response.AuthRegisterResponse;
import com.titta.api.features.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Autowired
    private AuthService authService;

    @PostMapping("/log-in")
    public ResponseEntity<AuthLoginResponse> login(
            @RequestBody @Valid AuthLoginRequest userRequest,
            HttpServletResponse response) {
        return ResponseEntity.ok(this.authService.loginUser(userRequest, response));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthRegisterRequest> register(
            @RequestBody @Valid AuthRegisterRequest registerRequest,
            HttpServletResponse response) {
        AuthRegisterResponse authRegisterRequest = authService.registerUser(registerRequest, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null) {
            return new ResponseEntity<>(Map.of("error", "Refresh token no encontrado"), HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(authService.refreshAccessToken(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {
        authService.logoutUser(refreshToken);
        ResponseCookie cookie = ResponseCookie.from("refresh_token", null)
                .maxAge(0)
                .httpOnly(true)
                .path("/api/v1/auth")
                // .secure(true) // Descomentar en producción
                .build();
        Map<String, String> responseBody = Map.of("message", "Logout exitoso");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(responseBody);
    }
}
