package com.titta.api.features.auth.dto.response;

import lombok.Builder;

@Builder
public record RefreshTokenResponse(
        String jwt,
        String message
) {
}
