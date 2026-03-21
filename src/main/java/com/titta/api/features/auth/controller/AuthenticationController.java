package com.titta.api.features.auth.controller;

import com.titta.api.features.auth.dto.request.AuthLoginRequest;
import com.titta.api.features.auth.dto.request.AuthRegisterRequest;
import com.titta.api.features.auth.dto.response.AuthLoginResponse;
import com.titta.api.features.auth.dto.response.AuthRegisterResponse;
import com.titta.api.features.auth.dto.response.RefreshTokenResponse;
import com.titta.api.features.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@PreAuthorize("permitAll()")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final String REFRESH_COOKIE_NAME = "refresh_token";
    private static final String REFRESH_COOKIE_PATH = "/api/v1/auth";
    private static final long REFRESH_COOKIE_SECONDS = 7L * 24 * 60 * 60;

    private final AuthService authService;

    @Value("${app.security.cookie.secure:false}")
    private boolean secureCookie;

    @PostMapping("/log-in")
    public ResponseEntity<AuthLoginResponse> login(
            @RequestBody @Valid AuthLoginRequest userRequest,
            HttpServletResponse response) {
        var result = authService.loginUser(userRequest);
        response.addHeader(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(result.refreshToken()).toString());
        return ResponseEntity.ok(result.response());
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthRegisterResponse> register(
            @RequestBody @Valid AuthRegisterRequest registerRequest) {
        AuthRegisterResponse authRegisterResponse = authService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(authRegisterResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var result = authService.refreshAccessToken(refreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(result.refreshToken()).toString());
        return ResponseEntity.ok(result.response());
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        authService.logoutUser(refreshToken, authorizationHeader);

        ResponseCookie clearedCookie = ResponseCookie.from(REFRESH_COOKIE_NAME, "")
                .maxAge(0)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Lax")
                .path(REFRESH_COOKIE_PATH)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearedCookie.toString())
                .body(Map.of("message", "Logout exitoso"));
    }

    private ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Lax")
                .path(REFRESH_COOKIE_PATH)
                .maxAge(REFRESH_COOKIE_SECONDS)
                .build();
    }
}
