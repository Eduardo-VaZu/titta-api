package com.titta.api.features.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequestDto(
        @NotBlank(message = "El nombre de la categoría no puede ser nulo o estar vacío.")
        @Size(max = 50, message = "El nombre de la categoría no debe exceder los 50 caracteres")
        String nombreCategoria
) {
}
