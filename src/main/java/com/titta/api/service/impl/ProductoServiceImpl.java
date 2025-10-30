package com.titta.api.service.impl;

import com.titta.api.dto.request.ProductoRequestDto;
import com.titta.api.dto.response.ProductoResponseDto;
import com.titta.api.exception.DuplicateResourceException;
import com.titta.api.exception.ResourceNotFoundException;
import com.titta.api.mapper.ProductoMapper;
import com.titta.api.model.Producto;
import com.titta.api.repository.ProductoRepository;
import com.titta.api.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProductoMapper productoMapper;

    @Override
    @Transactional
    public ProductoResponseDto crearProducto(ProductoRequestDto productoDto) {
        if (productoRepository.existsBySku(productoDto.sku())) {
            throw new DuplicateResourceException("Ya existe un producto con el SKU '" + productoDto.sku() + "'.");
        }

        Producto producto = productoMapper.toProducto(productoDto);
        Producto nuevoProducto = productoRepository.save(producto);
        return productoMapper.toResponseDto(nuevoProducto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDto> obtenerTodosLosProductos() {
        return productoRepository.findAll()
                .stream()
                .map(productoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDto obtenerProductoPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
        return productoMapper.toResponseDto(producto);
    }
}