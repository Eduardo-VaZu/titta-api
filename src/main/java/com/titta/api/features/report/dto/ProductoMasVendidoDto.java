package com.titta.api.features.report.dto;

public record ProductoMasVendidoDto(
        Long idProducto,
        String nombreProducto,
        String sku,
        Long totalUnidadesVendidas
) {
}