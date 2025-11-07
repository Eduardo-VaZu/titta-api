package com.titta.api.features.category.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequestDto(

        @NotBlank(message = "El nombre de la categoría no puede ser nulo o estar vacío.")
        String nombreCategoria
) {
}
