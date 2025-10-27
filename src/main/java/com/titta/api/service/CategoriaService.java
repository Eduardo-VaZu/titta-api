package com.titta.api.service;

import com.titta.api.dto.request.CategoriaRequestDto;
import com.titta.api.dto.response.CategoriaResponseDto;
import com.titta.api.model.Categoria;

import java.util.List;

public interface CategoriaService {

    CategoriaResponseDto crearCategoria(CategoriaRequestDto categoriaDto);
    List<CategoriaResponseDto> obterCategorias();

}
