package com.titta.api.features.category;

import com.titta.api.features.category.dto.CategoriaRequestDto;
import com.titta.api.features.category.dto.CategoriaResponseDto;
import com.titta.api.domain.model.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public Categoria toCategoria(CategoriaRequestDto dto) {
        if (dto == null) {
            return null;
        }
        return Categoria.builder()
                .nombreCategoria(dto.nombreCategoria())
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
