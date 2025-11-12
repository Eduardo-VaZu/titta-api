package com.titta.api.features.inventory.service.impl;

import com.titta.api.config.exception.ResourceNotFoundException;
import com.titta.api.domain.model.*;
import com.titta.api.domain.model.enums.TipoMovimientoInventario;
import com.titta.api.domain.repository.MovimientoInventarioRepository;
import com.titta.api.domain.repository.ProductoRepository;
import com.titta.api.domain.repository.SedeRepository;
import com.titta.api.domain.repository.StockSedeRepository;
import com.titta.api.features.inventory.dto.response.StockSedeResponseDto;
import com.titta.api.features.inventory.mapper.InventarioMapper;
import com.titta.api.features.inventory.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventarioServiceImpl implements InventarioService {

    @Autowired
    private StockSedeRepository stockSedeRepository;
    @Autowired
    private MovimientoInventarioRepository movimientoInventarioRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private SedeRepository sedeRepository;
    @Autowired
    private InventarioMapper inventarioMapper;

    @Override
    @Transactional
    public StockSedeResponseDto ajustarStock(Long idProducto, Long idSede, int cantidad, String razon) {

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + idProducto));

        Sede sede = sedeRepository.findById(idSede)
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada con ID: " + idSede));

        StockSedeId stockSedeId = new StockSedeId(idProducto, idSede);
        StockSede stockSede = stockSedeRepository.findById(stockSedeId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró stock para el producto " + idProducto + " en la sede " + idSede));

        int stockAntiguo = stockSede.getCantidad();
        int stockNuevo = stockAntiguo + cantidad;

        if (stockNuevo < 0) {
            throw new IllegalArgumentException("El ajuste resultaría en stock negativo (" + stockNuevo + "). Stock actual: " + stockAntiguo);
        }
        stockSede.setCantidad(stockNuevo);
        StockSede stockActualizado = stockSedeRepository.save(stockSede);

        MovimientoInventario movimiento = MovimientoInventario.builder()
                .producto(producto)
                .sede(sede)
                .tipoMovimiento(TipoMovimientoInventario.AJUSTE_MANUAL)
                .cantidad(cantidad)
                .fechaMovimiento(LocalDate.now())
                .razon(razon)
                .build();

        movimientoInventarioRepository.save(movimiento);

        return inventarioMapper.toStockSedeResponseDto(stockActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockSedeResponseDto> listarSedeId(Long idSede) {

        if (!sedeRepository.existsById(idSede)) {
            throw new ResourceNotFoundException("Sede no encontrada con ID: " + idSede);
        }

        List<StockSede> stocks = stockSedeRepository.findAllById_IdSede(idSede);

        return stocks.stream()
                .map(inventarioMapper::toStockSedeResponseDto)
                .collect(Collectors.toList());
    }
}
