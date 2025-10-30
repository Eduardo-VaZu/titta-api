package com.titta.api.dto.response;

import lombok.Builder;

@Builder
public record CategoriaResponseDto(
        Long idCategoria,
        String nombreCategoria
) {

}