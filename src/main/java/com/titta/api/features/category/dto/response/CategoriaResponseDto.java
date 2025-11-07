package com.titta.api.features.category.dto.response;

import lombok.Builder;

@Builder
public record CategoriaResponseDto(
        Long idCategoria,
        String nombreCategoria
) {

}