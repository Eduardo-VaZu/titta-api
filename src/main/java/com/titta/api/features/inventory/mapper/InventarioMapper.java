package com.titta.api.features.inventory.mapper;

import com.titta.api.domain.model.StockSede;
import com.titta.api.features.inventory.dto.response.StockSedeResponseDto;
import org.springframework.stereotype.Component;

@Component
public class InventarioMapper {

    public StockSedeResponseDto toStockSedeResponseDto(StockSede stockSede) {

        if (stockSede == null) {
            return null;
        }

        return new StockSedeResponseDto(
                stockSede.getSede().getIdSede(),
                stockSede.getSede().getNombreSede(),
                stockSede.getCantidad()
        );
    }
}