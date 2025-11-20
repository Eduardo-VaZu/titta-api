package com.titta.api.features.report.service.impl;

import com.titta.api.domain.model.MovimientoInventario;
import com.titta.api.domain.repository.DetalleVentaRepository;
import com.titta.api.domain.repository.MovimientoInventarioRepository;
import com.titta.api.domain.repository.StockSedeRepository;
import com.titta.api.features.report.dto.ProductoBajoStockDto;
import com.titta.api.features.report.dto.ProductoMasVendidoDto;
import com.titta.api.features.report.mapper.ReportMapper;
import com.titta.api.features.report.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final StockSedeRepository stockSedeRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final ReportMapper reportMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoBajoStockDto> obtenerProductosBajoStock(int umbral) {
        return stockSedeRepository.findByCantidadLessThan(umbral).stream()
                .map(reportMapper::toProductoBajoStockDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoMasVendidoDto> obtenerTopProductosVendidos(int limite) {
        Pageable pageable = PageRequest.of(0, limite);
        return detalleVentaRepository.findProductosMasVendidos(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovimientoInventario> obtenerHistorialMovimientos(Pageable pageable) {
        return movimientoInventarioRepository.findAll(pageable);
    }
}