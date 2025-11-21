package com.titta.api.features.report.dto;

import java.time.LocalDate;

public record MovimientoInventarioResponseDto(
        Long idMovimiento,
        String tipoMovimiento,
        int cantidad,
        LocalDate fechaMovimiento,
        String razon,

        Long idProducto,
        String nombreProducto,
        String sku,

        Long idSede,
        String nombreSede
) {
}