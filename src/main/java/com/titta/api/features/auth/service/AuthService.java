package com.titta.api.features.auth.service;

import com.titta.api.features.auth.dto.request.AuthLoginRequest;
import com.titta.api.features.auth.dto.request.AuthRegisterRequest;
import com.titta.api.features.auth.dto.response.AuthResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface AuthService {

    AuthResponse registerUser(AuthRegisterRequest registerRequest, HttpServletResponse response);

    AuthResponse loginUser(AuthLoginRequest authLoginRequest, HttpServletResponse response);

    Map<String, String> refreshAccessToken(String refreshToken);

    void logoutUser(String refreshToken);
}