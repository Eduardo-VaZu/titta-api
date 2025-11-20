package com.titta.api.features.sale.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SaleResponseDto(
        Long idVenta,
        LocalDateTime fechaVenta,
        BigDecimal total,
        String estado,
        String metodoPago,
        int cantidadProductos
) {
}