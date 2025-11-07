package com.titta.api.features.category.service;

import com.titta.api.features.category.dto.request.CategoriaRequestDto;
import com.titta.api.features.category.dto.response.CategoriaResponseDto;

import java.util.List;

public interface CategoriaService {

    CategoriaResponseDto crearCategoria(CategoriaRequestDto categoriaDto);
    List<CategoriaResponseDto> obterCategorias();

}
