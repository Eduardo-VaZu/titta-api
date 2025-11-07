package com.titta.api.features.category;

import com.titta.api.features.category.dto.CategoriaRequestDto;
import com.titta.api.features.category.dto.CategoriaResponseDto;

import java.util.List;

public interface CategoriaService {

    CategoriaResponseDto crearCategoria(CategoriaRequestDto categoriaDto);
    List<CategoriaResponseDto> obterCategorias();

}
