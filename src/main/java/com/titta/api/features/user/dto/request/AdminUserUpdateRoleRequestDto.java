package com.titta.api.features.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AdminUserUpdateRoleRequestDto(
        @NotBlank(message = "El nombre del rol es obligatorio")
        @Pattern(regexp = "ADMINISTRADOR|CLIENTE|EMPLEADO", message = "El rol debe ser 'ADMINISTRADOR', 'CLIENTE' o 'EMPLEADO'")
        String rolNombre
) {
}