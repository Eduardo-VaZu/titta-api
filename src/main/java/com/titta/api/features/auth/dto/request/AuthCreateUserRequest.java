package com.titta.api.features.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthCreateUserRequest(
        @NotBlank
        String username,

        @NotBlank
        String password
) {
}
