package com.titta.api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ProductoResponseDto(
        String nombreProducto,
        String sku,
        String descripcion,
        BigDecimal precio,
        Boolean estadoProducto,
        CategoriaResponseDto categoria,
        ImagenResponseDto imagenes,
        List<StockSedeResponseDto> stockSede
) {

    public record CategoriaResponseDto(
            Long idCategoria,
            String nombreCategoria
    ) {

    }

    public record ImagenResponseDto(
            Long idImagen,
            String imagenUrl,
            String altText
    ) {

    }

    public record StockSedeResponseDto(
            Long idSede,
            String nombreSede,
            int stock
    ) {

    }
}
