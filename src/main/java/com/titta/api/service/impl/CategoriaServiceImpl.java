package com.titta.api.service.impl;

import com.titta.api.dto.request.CategoriaRequestDto;
import com.titta.api.dto.response.CategoriaResponseDto;
import com.titta.api.exception.DuplicateResourceException;
import com.titta.api.mapper.CategoriaMapper;
import com.titta.api.model.Categoria;
import com.titta.api.repository.CategoriaRepository;
import com.titta.api.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private CategoriaMapper categoriaMapper;

    @Override
    public CategoriaResponseDto crearCategoria(CategoriaRequestDto categoriaDto) {

        if (categoriaRepository.existsByNombreCategoria(categoriaDto.nombreCategoria())) {
            throw new DuplicateResourceException("Ya existe una categoría con el nombre '" + categoriaDto.nombreCategoria() + "'.");
        }

        Categoria categoria = categoriaMapper.toCategoria(categoriaDto);
        Categoria nuevaCategoria = categoriaRepository.save(categoria);

        return categoriaMapper.toCategoriaResponseDto(nuevaCategoria);
    }

    @Override
    public List<CategoriaResponseDto> obterCategorias() {
        return categoriaRepository.findAll()
                .stream()
                .map(categoriaMapper::toCategoriaResponseDto)
                .collect(Collectors.toList());
    }
}
