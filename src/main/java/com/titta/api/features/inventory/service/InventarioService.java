package com.titta.api.features.inventory.service;


import com.titta.api.features.inventory.dto.response.StockSedeResponseDto;

public interface InventarioService {

    StockSedeResponseDto ajustarStock(Long idProducto, Long idSede, int cantidad, String razon);

}