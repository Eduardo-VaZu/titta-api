package com.titta.api.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class ProductoRequestDto {
    private String nombreProducto;
    private String sku;
    private String descripcion;
    private BigDecimal precio;
    private boolean estadoProducto;
    private Long idCategoria;
    private InagenRequestDto imagen;
    private List<StockSedeRequestDto> stockSede;

    @Data
    public static class InagenRequestDto {
        private String imagenUrl;
    }

    @Data
    public static class StockSedeRequestDto {
        private Long idSede;
        private int stock;
    }
}
