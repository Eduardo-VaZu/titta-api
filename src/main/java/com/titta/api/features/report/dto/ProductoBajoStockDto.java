package com.titta.api.features.report.dto;

public record ProductoBajoStockDto(
        Long idSede,
        String nombreSede,
        Long idProducto,
        String nombreProducto,
        String sku,
        int stockActual
) {
}