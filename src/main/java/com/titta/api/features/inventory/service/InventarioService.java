package com.titta.api.features.inventory.service;


import com.titta.api.features.inventory.dto.response.StockSedeResponseDto;

import java.util.List;

public interface InventarioService {

    StockSedeResponseDto ajustarStock(Long idProducto, Long idSede, int cantidad, String razon);

    List<StockSedeResponseDto> listarSedeId(Long idSede);

}