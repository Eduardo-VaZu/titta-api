package com.titta.api.features.report.mapper;

import com.titta.api.domain.model.StockSede;
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
}
