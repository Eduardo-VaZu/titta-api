package com.titta.api.features.product.dto.response;

import com.titta.api.features.inventory.dto.response.StockSedeResponseDto;

import java.math.BigDecimal;
import java.util.List;

public record ProductoResponseDto(
        Long idProducto,
        String nombreProducto,
        String sku,
        String descripcion,
        BigDecimal precio,
        Boolean estadoProducto,
        Long idCategoria,
        ImagenResponseDto imagen,
        List<StockSedeResponseDto> stocks
) {

    public record ImagenResponseDto(
            Long idImagen,
            String imagenUrl,
            String altText
    ) {
    }

}