package com.titta.api.features.auth.service;

import com.titta.api.features.auth.dto.request.AuthLoginRequest;
import com.titta.api.features.auth.dto.request.AuthRegisterRequest;
import com.titta.api.features.auth.dto.response.AuthLoginResponse;
import com.titta.api.features.auth.dto.response.AuthRegisterResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface AuthService {

    AuthRegisterResponse registerUser(AuthRegisterRequest registerRequest, HttpServletResponse response);

    AuthLoginResponse loginUser(AuthLoginRequest authLoginRequest, HttpServletResponse response);

    Map<String, String> refreshAccessToken(String refreshToken);

    void logoutUser(String refreshToken);
}