package com.titta.api.service.impl;

import com.titta.api.model.Categoria;
import com.titta.api.repository.CategoriaRepository;
import com.titta.api.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;


    @Override
    public Categoria crearCategoria(Categoria categoria) {
        return null;
    }

    @Override
    public List<Categoria> obterCategorias() {
        return List.of();
    }
}
