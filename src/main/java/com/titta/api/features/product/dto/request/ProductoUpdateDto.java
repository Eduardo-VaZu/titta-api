package com.titta.api.features.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ProductoUpdateDto(

        @NotBlank(message = "El nombre del producto es obligatorio")
        String nombreProducto,

        String descripcion,

        @NotNull(message = "El precio no puede ser nulo")
        @Positive(message = "El precio debe ser positivo")
        BigDecimal precio,

        @NotNull(message = "El estado del producto es obligatorio")
        Boolean estadoProducto,

        @NotNull(message = "El ID de la categoría es obligatorio")
        Long idCategoria
) {
}