package com.titta.api.features.product.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductoUpdateDetailsRequestDto(
        @NotBlank(message = "El nombre del producto es obligatorio")
        String nombreProducto,

        @NotBlank(message = "El SKU es obligatorio")
        String sku,

        String descripcion,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
        BigDecimal precio,

        @NotNull(message = "El id de categoría es obligatorio")
        Long idCategoria
) {}
