package com.titta.api.features.inventory.service;


import com.titta.api.features.inventory.dto.request.UpdateStockRequestDto;
import com.titta.api.features.inventory.dto.response.StockSedeResponseDto;

import java.util.List;

public interface InventarioService {

    StockSedeResponseDto ajustarStock(Long idProducto, Long idSede, UpdateStockRequestDto stockDto);

    List<StockSedeResponseDto> listarSedeId(Long idSede);

}