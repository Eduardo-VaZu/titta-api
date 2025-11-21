package com.titta.api.features.category.mapper;

import com.titta.api.domain.model.Categoria;
import com.titta.api.features.category.dto.request.CategoriaRequestDto;
import com.titta.api.features.category.dto.response.CategoriaResponseDto;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public Categoria toCategoria(CategoriaRequestDto dto) {
        if (dto == null) {
            return null;
        }
        return Categoria.builder()
                .nombreCategoria(dto.nombreCategoria())
                .estado(true)
                .build();
    }

    public CategoriaResponseDto toCategoriaResponseDto(Categoria categoria) {
        if (categoria == null) {
            return null;
        }

        return new CategoriaResponseDto(
                categoria.getIdCategoria(),
                categoria.getNombreCategoria()
        );
    }

}
