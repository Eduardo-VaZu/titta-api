package com.titta.api.features.product.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductoUpdateDto(

        @NotBlank(message = "El nombre del producto es obligatorio")
        @Size(max = 100, message = "El nombre no debe exceder los 100 caracteres")
        String nombreProducto,

        String descripcion,

        @NotNull(message = "El precio no puede ser nulo")
        @Positive(message = "El precio debe ser positivo")
        @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 8 dígitos enteros y 2 decimales")
        BigDecimal precio,

        @NotNull(message = "El estado del producto es obligatorio")
        Boolean estadoProducto,

        @NotNull(message = "El ID de la categoría es obligatorio")
        Long idCategoria
) {
}