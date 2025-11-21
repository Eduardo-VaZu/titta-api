package com.titta.api.features.report.service;

import com.titta.api.features.report.dto.MovimientoInventarioResponseDto;
import com.titta.api.features.report.dto.ProductoBajoStockDto;
import com.titta.api.features.report.dto.ProductoMasVendidoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReporteService {
    List<ProductoBajoStockDto> obtenerProductosBajoStock(int umbral);

    List<ProductoMasVendidoDto> obtenerTopProductosVendidos(int limite);

    Page<MovimientoInventarioResponseDto> obtenerHistorialMovimientos(Pageable pageable);
}