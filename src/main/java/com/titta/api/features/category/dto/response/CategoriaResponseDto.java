package com.titta.api.features.category.dto;

import lombok.Builder;

@Builder
public record CategoriaResponseDto(
        Long idCategoria,
        String nombreCategoria
) {

}