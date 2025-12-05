package com.titta.api.features.auth.controller;

import com.titta.api.features.auth.dto.request.AuthLoginRequest;
import com.titta.api.features.auth.dto.request.AuthRegisterRequest;
import com.titta.api.features.auth.dto.response.AuthLoginResponse;
import com.titta.api.features.auth.dto.response.AuthRegisterResponse;
import com.titta.api.features.auth.dto.response.RefreshTokenResponse;
import com.titta.api.features.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@PreAuthorize("permitAll()")
public class AuthenticationController {

    @Autowired
    private AuthService authService;

    @Value("${app.security.cookie.secure:false}")
    private boolean secureCookie;

    @PostMapping("/log-in")
    public ResponseEntity<AuthLoginResponse> login(
            @RequestBody @Valid AuthLoginRequest userRequest,
            HttpServletResponse response) {
        var result = this.authService.loginUser(userRequest);
        response.addCookie(createRefreshTokenCookie(result.refreshToken()));
        return ResponseEntity.ok(result.response());
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthRegisterResponse> register(
            @RequestBody @Valid AuthRegisterRequest registerRequest,
            HttpServletResponse response) {
        AuthRegisterResponse authRegisterResponse = authService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(authRegisterResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var result = authService.refreshAccessToken(refreshToken);
        response.addCookie(createRefreshTokenCookie(result.refreshToken()));
        return ResponseEntity.ok(result.response());
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        authService.logoutUser(refreshToken, authorizationHeader);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", null)
                .maxAge(0)
                .httpOnly(true)
                .secure(secureCookie)
                .path("/api/v1/auth")
                .build();
        Map<String, String> responseBody = Map.of("message", "Logout exitoso");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(responseBody);
    }

    private Cookie createRefreshTokenCookie(String token) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", token);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/api/v1/auth");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        refreshTokenCookie.setSecure(secureCookie);
        return refreshTokenCookie;
    }
}
