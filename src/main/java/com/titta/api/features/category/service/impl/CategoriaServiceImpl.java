package com.titta.api.features.category;

import com.titta.api.features.category.dto.CategoriaRequestDto;
import com.titta.api.features.category.dto.CategoriaResponseDto;
import com.titta.api.config.exception.DuplicateResourceException;
import com.titta.api.domain.model.Categoria;
import com.titta.api.domain.repository.CategoriaRepository;
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
