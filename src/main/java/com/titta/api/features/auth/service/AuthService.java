package com.titta.api.features.auth.service;

import com.titta.api.features.auth.dto.request.AuthLoginRequest;
import com.titta.api.features.auth.dto.request.AuthRegisterRequest;
import com.titta.api.features.auth.dto.response.AuthRegisterResponse;
import com.titta.api.features.auth.dto.result.AuthLoginResult;
import com.titta.api.features.auth.dto.result.AuthRefreshResult;

public interface AuthService {

    AuthRegisterResponse registerUser(AuthRegisterRequest registerRequest);

    AuthLoginResult loginUser(AuthLoginRequest authLoginRequest);

    AuthRefreshResult refreshAccessToken(String refreshToken);

    void logoutUser(String refreshToken, String authorizationHeader);
}