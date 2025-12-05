package com.titta.api.features.auth.dto.result;

import com.titta.api.features.auth.dto.response.RefreshTokenResponse;
import lombok.Builder;

@Builder
public record AuthRefreshResult(
        RefreshTokenResponse response,
        String refreshToken) {
}
