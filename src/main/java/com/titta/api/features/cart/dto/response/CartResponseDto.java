package com.titta.api.features.cart.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CartResponseDto(
        Long idCarrito,
        Long idSede,
        String nombreSede,
        String estado,
        List<ItemCarritoResponseDto> items,
        BigDecimal subTotal,
        int totalItems
) {

    public record ItemCarritoResponseDto(
            Long idProducto,
            String nombreProducto,
            String sku,
            String imagenUrl,
            BigDecimal precioUnitario,
            int cantidad,
            BigDecimal subTotalItem
    ) {
    }
}