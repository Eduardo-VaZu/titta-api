package com.titta.api.features.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateProfileRequestDto(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede contener letras y espacios")
        String nombre,

        @NotBlank(message = "El apellido paterno es obligatorio")
        @Size(min = 2, max = 100, message = "El apellido paterno debe tener entre 2 y 100 caracteres")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El apellido paterno solo puede contener letras y espacios")
        String apellidoPaterno,

        @Size(max = 100, message = "El apellido materno no debe exceder los 100 caracteres")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$", message = "El apellido materno solo puede contener letras y espacios")
        String apellidoMaterno
) {
}