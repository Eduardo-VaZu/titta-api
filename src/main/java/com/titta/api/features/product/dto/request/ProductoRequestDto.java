package com.titta.api.features.product.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductoRequestDto(
        String nombreProducto,
        String sku,
        String descripcion,
        BigDecimal precio,
        Boolean estadoProducto,
        Long idCategoria,
        InagenRequestDto imagen,
        List<StockSedeRequestDto> stockSede
) {

    public record InagenRequestDto(
            String imagenUrl,
            String altText
    ) {
    }

    public record StockSedeRequestDto(
            Long idSede,
            int stock
    ) {
    }
}
