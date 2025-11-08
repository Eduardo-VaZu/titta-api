package com.titta.api.features.inventory.dto.response;

public record StockSedeResponseDto(
        Long idSede,
        String nombreSede,
        int stock
) {

}
