package com.titta.api.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class ProductoResponseDto {
    private String nombreProducto;
    private String sku;
    private String descripcion;
    private BigDecimal precio;
    private Boolean estadoProducto;
    private CategoriaResponseDto categoria;
    private ImagenResponseDto imagenes;
    private List<StockSedeResponseDto> stockSede;

    @Data
    public static class CategoriaResponseDto {
        private Long idCategoria;
        private String nombreCategoria;
    }

    @Data
    public static class ImagenResponseDto {
        private Long idImagen;
        private String imagenUrl;
        private String altText;
    }

    @Data
    public static class StockSedeResponseDto {
        private Long idSede;
        private String nombreSede;
        private int stock;
    }
}
