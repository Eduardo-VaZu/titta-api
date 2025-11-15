package com.titta.api.features.user.dto.request;

import jakarta.validation.constraints.NotNull;

public record AdminUserUpdateStatusRequestDto(
        @NotNull(message = "El estado no puede ser nulo")
        Boolean estado
) {
}