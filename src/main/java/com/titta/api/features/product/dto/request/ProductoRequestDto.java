package com.titta.api.features.product.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.List;

public record ProductoRequestDto(

        @NotBlank(message = "El nombre del producto es obligatorio")
        @Size(max = 100, message = "El nombre no debe exceder los 100 caracteres")
        String nombreProducto,

        @NotBlank(message = "El SKU es obligatorio")
        @Size(max = 50, message = "El SKU no debe exceder los 50 caracteres")
        String sku,

        String descripcion,

        @NotNull(message = "El precio no puede ser nulo")
        @Positive(message = "El precio debe ser positivo")
        @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 8 dígitos enteros y 2 decimales") // Basado en DECIMAL(10, 2)
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
            @Size(max = 255, message = "La URL no debe exceder los 255 caracteres")
            @URL(message = "Debe ser una URL válida")
            String imagenUrl,

            @NotBlank(message = "El texto alternativo es obligatorio")
            @Size(max = 255, message = "El texto alternativo no debe exceder los 255 caracteres")
            String altText
    ) {
    }

    public record StockSedeRequestDto(
            @NotNull(message = "El ID de la sede es obligatorio")
            Long idSede,

            @NotNull(message = "La cantidad de stock es obligatoria")
            @Positive(message = "El stock debe ser un número positivo")
            Integer cantidad
    ) {
    }
}