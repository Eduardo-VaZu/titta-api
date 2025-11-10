package com.titta.api.features.product.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public record ProductoRequestDto(

        @NotBlank(message = "El nombre del producto es obligatorio")
        String nombreProducto,

        @NotBlank(message = "El SKU es obligatorio")
        String sku,

        String descripcion,

        @NotNull(message = "El precio no puede ser nulo")
        @Positive(message = "El precio debe ser positivo")
        BigDecimal precio,

        @NotNull(message = "El estado del producto es obligatorio")
        Boolean estadoProducto,

        @NotNull(message = "El ID de la categoría es obligatorio")
        Long idCategoria,

        @NotNull(message = "La imagen es obligatoria")
        @Valid
        ImagenRequestDto imagen,

        @NotNull(message = "La lista de stock no puede ser nula")
        @Valid
        List<StockSedeRequestDto> stocks
) {

    public record ImagenRequestDto(
            @NotBlank(message = "La URL de la imagen es obligatoria")
            String imagenUrl,

            @NotBlank(message = "El texto alternativo es obligatorio")
            String altText
    ) {}

    public record StockSedeRequestDto(
            @NotNull(message = "El ID de la sede es obligatorio")
            Long idSede,

            @NotNull(message = "La cantidad de stock es obligatoria")
            @Positive(message = "El stock debe ser un número positivo")
            Integer cantidad
    ) {}
}