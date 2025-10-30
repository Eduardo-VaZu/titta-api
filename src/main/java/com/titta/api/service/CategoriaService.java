package com.titta.api.service;

import com.titta.api.dto.response.CategoriaResponseDto;

import java.util.List;

public interface CategoriaService {

    CategoriaResponseDto crearCategoria(CategoriaRequestDto categoriaDto);
    List<CategoriaResponseDto> obterCategorias();

}
