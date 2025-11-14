package com.titta.api.features.category.service.impl;

import com.titta.api.config.exception.DuplicateResourceException;
import com.titta.api.config.exception.ResourceNotFoundException;
import com.titta.api.domain.model.Categoria;
import com.titta.api.domain.repository.CategoriaRepository;
import com.titta.api.features.category.dto.request.CategoriaRequestDto;
import com.titta.api.features.category.dto.response.CategoriaResponseDto;
import com.titta.api.features.category.mapper.CategoriaMapper;
import com.titta.api.features.category.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private CategoriaMapper categoriaMapper;

    @Override
    @Transactional
    public CategoriaResponseDto crearCategoria(CategoriaRequestDto categoriaDto) {

        if (categoriaRepository.existsByNombreCategoria(categoriaDto.nombreCategoria())) {
            throw new DuplicateResourceException("Ya existe una categoría con el nombre '" + categoriaDto.nombreCategoria() + "'.");
        }

        Categoria categoria = categoriaMapper.toCategoria(categoriaDto);
        Categoria nuevaCategoria = categoriaRepository.save(categoria);

        return categoriaMapper.toCategoriaResponseDto(nuevaCategoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDto> obterCategorias() {
        return categoriaRepository.findAllByEstado(true)
                .stream()
                .map(categoriaMapper::toCategoriaResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDto getCategoriaById(Long idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con ID: " + idCategoria));

        return categoriaMapper.toCategoriaResponseDto(categoria);
    }

    @Override
    @Transactional
    public CategoriaResponseDto updateCategoria(Long idCategoria, CategoriaRequestDto categoriaDto) {

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con ID: " + idCategoria));

        String nuevoNombre = categoriaDto.nombreCategoria();

        Optional<Categoria> categoriaConMismoNombre = categoriaRepository.findByNombreCategoria(nuevoNombre);

        if (categoriaConMismoNombre.isPresent() && !categoriaConMismoNombre.get().getIdCategoria().equals(idCategoria)) {
            throw new DuplicateResourceException("Ya existe otra categoría con el nombre '" + nuevoNombre + "'.");
        }

        categoria.setNombreCategoria(nuevoNombre);
        Categoria categoriaActualizada = categoriaRepository.save(categoria);

        return categoriaMapper.toCategoriaResponseDto(categoriaActualizada);
    }

    @Override
    @Transactional
    public void deleteCategoria(Long idCategoria) {

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con ID: " + idCategoria));

        if (!categoria.getProductos().isEmpty()) {
            throw new DataIntegrityViolationException("No se puede desactivar la categoría (ID: " + idCategoria + "). Aún tiene productos asociados.");
        }

        categoria.setEstado(false);
        categoriaRepository.save(categoria);
    }
}
