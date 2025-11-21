package com.titta.api.features.report.mapper;

import com.titta.api.domain.model.MovimientoInventario;
import com.titta.api.domain.model.StockSede;
import com.titta.api.features.report.dto.MovimientoInventarioResponseDto;
import com.titta.api.features.report.dto.ProductoBajoStockDto;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public ProductoBajoStockDto toProductoBajoStockDto(StockSede stock) {
        if (stock == null) {
            return null;
        }

        return new ProductoBajoStockDto(
                stock.getSede() != null ? stock.getSede().getIdSede() : null,
                stock.getSede() != null ? stock.getSede().getNombreSede() : null,
                stock.getProducto() != null ? stock.getProducto().getIdProducto() : null,
                stock.getProducto() != null ? stock.getProducto().getNombreProducto() : null,
                stock.getProducto() != null ? stock.getProducto().getSku() : null,
                stock.getCantidad()
        );
    }

    public MovimientoInventarioResponseDto toMovimientoResponseDto(MovimientoInventario movimiento) {
        if (movimiento == null) {
            return null;
        }

        return new MovimientoInventarioResponseDto(
                movimiento.getIdMovimientoInventario(),
                movimiento.getTipoMovimiento() != null ? movimiento.getTipoMovimiento().name() : null,
                movimiento.getCantidad(),
                movimiento.getFechaMovimiento(),
                movimiento.getRazon(),

                movimiento.getProducto() != null ? movimiento.getProducto().getIdProducto() : null,
                movimiento.getProducto() != null ? movimiento.getProducto().getNombreProducto() : null,
                movimiento.getProducto() != null ? movimiento.getProducto().getSku() : null,

                movimiento.getSede() != null ? movimiento.getSede().getIdSede() : null,
                movimiento.getSede() != null ? movimiento.getSede().getNombreSede() : null
        );
    }
}
