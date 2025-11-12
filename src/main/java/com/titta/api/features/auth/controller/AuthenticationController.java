package com.titta.api.features.auth.controller;

import com.titta.api.features.auth.dto.request.AuthLoginRequest;
import com.titta.api.features.auth.dto.request.AuthRegisterRequest;
import com.titta.api.features.auth.dto.response.AuthResponse;
import com.titta.api.features.auth.service.AuthService;
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
public class AuthenticationController {

    @Autowired
    private AuthService authService;

    @PostMapping("/log-in")
    public ResponseEntity<AuthResponse> login(
            @RequestBody @Valid AuthLoginRequest userRequest, HttpServletResponse response) {

        AuthResponse authResponse = this.authService.loginUser(userRequest, response);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> register(
            @RequestBody @Valid AuthRegisterRequest registerRequest, HttpServletResponse response) {

        AuthResponse authResponse = authService.registerUser(registerRequest, response);
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(
            @CookieValue(name = "refresh_token") String refreshToken) {

        Map<String, String> response = authService.refreshAccessToken(refreshToken);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
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
