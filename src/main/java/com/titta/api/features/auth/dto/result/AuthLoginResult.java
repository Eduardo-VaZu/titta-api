package com.titta.api.features.auth.dto.result;

import com.titta.api.features.auth.dto.response.AuthLoginResponse;
import lombok.Builder;

@Builder
public record AuthLoginResult(
        AuthLoginResponse response,
        String refreshToken) {
}
