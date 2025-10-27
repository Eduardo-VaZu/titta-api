package com.titta.api.service;

import com.titta.api.model.Categoria;

import java.util.List;

public interface CategoriaService {

    Categoria crearCategoria(Categoria categoria);
    List<Categoria> obterCategorias();

}
