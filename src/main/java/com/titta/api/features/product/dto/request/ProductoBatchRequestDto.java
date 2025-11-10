package com.titta.api.features.product.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ProductoBatchRequestDto(

        @NotEmpty(message = "La lista de productos no puede estar vacía.")
        @Valid
        List<ProductoRequestDto> productos
) {
}
