package com.titta.api.features.auth.service;

import com.titta.api.features.auth.dto.request.AuthLoginRequest;
import com.titta.api.features.auth.dto.request.AuthRegisterRequest;
import com.titta.api.features.auth.dto.response.AuthLoginResponse;
import com.titta.api.features.auth.dto.response.AuthRegisterResponse;
import com.titta.api.features.auth.dto.response.RefreshTokenResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    AuthRegisterResponse registerUser(AuthRegisterRequest registerRequest, HttpServletResponse response);

    AuthLoginResponse loginUser(AuthLoginRequest authLoginRequest, HttpServletResponse response);

    RefreshTokenResponse refreshAccessToken(String refreshToken, HttpServletResponse response);

    void logoutUser(String refreshToken, String authorizationHeader);
}