package com.titta.api.features.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateStockRequestDto(
        @NotNull(message = "La cantidad no puede ser nula")
        Integer cantidad,

        @NotBlank(message = "Se requiere una razón para el ajuste")
        String razon
) {}
