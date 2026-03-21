package com.titta.api.features.inventory.service.impl;

import com.titta.api.config.exception.ResourceNotFoundException;
import com.titta.api.domain.model.MovimientoInventario;
import com.titta.api.domain.model.Producto;
import com.titta.api.domain.model.Sede;
import com.titta.api.domain.model.StockSede;
import com.titta.api.domain.model.enums.TipoMovimientoInventario;
import com.titta.api.domain.repository.MovimientoInventarioRepository;
import com.titta.api.domain.repository.ProductoRepository;
import com.titta.api.domain.repository.SedeRepository;
import com.titta.api.domain.repository.StockSedeRepository;
import com.titta.api.features.inventory.dto.request.UpdateStockRequestDto;
import com.titta.api.features.inventory.dto.response.StockSedeResponseDto;
import com.titta.api.features.inventory.mapper.InventarioMapper;
import com.titta.api.features.inventory.service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioService {

    private final StockSedeRepository stockSedeRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final ProductoRepository productoRepository;
    private final SedeRepository sedeRepository;
    private final InventarioMapper inventarioMapper;

    @Override
    @Transactional
    public StockSedeResponseDto ajustarStock(Long idProducto, Long idSede, UpdateStockRequestDto stockDto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + idProducto));

        Sede sede = sedeRepository.findById(idSede)
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada con ID: " + idSede));

        StockSede stockSede = stockSedeRepository.findByIdForUpdate(idProducto, idSede)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro stock para el producto " + idProducto + " en la sede " + idSede));

        int stockAntiguo = stockSede.getCantidad();
        int stockNuevo = stockAntiguo + stockDto.cantidad();

        if (stockNuevo < 0) {
            throw new IllegalArgumentException(
                    "El ajuste resultaria en stock negativo (" + stockNuevo + "). Stock actual: " + stockAntiguo);
        }

        stockSede.setCantidad(stockNuevo);
        StockSede stockActualizado = stockSedeRepository.save(stockSede);

        MovimientoInventario movimiento = MovimientoInventario.builder()
                .producto(producto)
                .sede(sede)
                .tipoMovimiento(TipoMovimientoInventario.AJUSTE_MANUAL)
                .cantidad(stockDto.cantidad())
                .fechaMovimiento(LocalDate.now())
                .razon(stockDto.razon())
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

    @Override
    @Transactional
    public void reducirStockPorVenta(Long idProducto, Long idSede, Integer cantidad) {
        StockSede stockSede = stockSedeRepository.findByIdForUpdate(idProducto, idSede)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Stock no encontrado para producto " + idProducto + " en sede " + idSede));

        int nuevoStock = stockSede.getCantidad() - cantidad;
        if (nuevoStock < 0) {
            throw new IllegalArgumentException(
                    "Stock insuficiente para el producto: " + stockSede.getProducto().getNombreProducto());
        }

        stockSede.setCantidad(nuevoStock);
        stockSedeRepository.save(stockSede);

        MovimientoInventario movimiento = MovimientoInventario.builder()
                .producto(stockSede.getProducto())
                .sede(stockSede.getSede())
                .tipoMovimiento(TipoMovimientoInventario.VENTA)
                .cantidad(cantidad)
                .fechaMovimiento(LocalDate.now())
                .razon("Venta realizada")
                .build();

        movimientoInventarioRepository.save(movimiento);
    }
}
