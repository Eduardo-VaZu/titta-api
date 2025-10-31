package com.titta.api.dto.auth;

import jakarta.validation.constraints.NotBlank;

// Usamos @NotBlank para asegurarnos de que el cliente no envíe estos campos vacíos.
public record AuthLoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
