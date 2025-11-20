package com.titta.api.features.report.controller;

import com.titta.api.domain.model.MovimientoInventario;
import com.titta.api.features.report.dto.ProductoBajoStockDto;
import com.titta.api.features.report.dto.ProductoMasVendidoDto;
import com.titta.api.features.report.service.ReporteService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
@PreAuthorize("hasAuthority('VER_REPORTES')")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/bajo-stock")
    public ResponseEntity<List<ProductoBajoStockDto>> getProductosBajoStock(
            @RequestParam(defaultValue = "10") int umbral) {
        return ResponseEntity.ok(reporteService.obtenerProductosBajoStock(umbral));
    }

    @GetMapping("/mas-vendidos")
    public ResponseEntity<List<ProductoMasVendidoDto>> getTopProductosVendidos(
            @RequestParam(defaultValue = "5") int limite) {
        return ResponseEntity.ok(reporteService.obtenerTopProductosVendidos(limite));
    }

    @GetMapping("/movimientos")
    public ResponseEntity<Page<MovimientoInventario>> getHistorialMovimientos(
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(reporteService.obtenerHistorialMovimientos(pageable));
    }
}
