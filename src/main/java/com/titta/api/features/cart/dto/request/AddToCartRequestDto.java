package com.titta.api.features.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddToCartRequestDto(

        @NotNull(message = "El ID del producto es obligatorio")
        Long idProducto,

        @NotNull(message = "El ID de la sede es obligatorio")
        Long idSede,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser como mínimo 1")
        Integer cantidad
) {
}
