package com.titta.api.features.sale.dto.request;

import jakarta.validation.constraints.NotNull;

public record SaleRequestDto(
        @NotNull(message = "El ID del método de pago es obligatorio")
        Long idMetodoPago
) {
}