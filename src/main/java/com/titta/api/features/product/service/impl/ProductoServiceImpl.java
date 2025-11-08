package com.titta.api.features.product.service.impl;

import com.titta.api.domain.model.Categoria;
import com.titta.api.domain.repository.CategoriaRepository;
import com.titta.api.features.product.dto.request.ProductoUpdateDetailsRequestDto;
import com.titta.api.features.product.mapper.ProductoMapper;
import com.titta.api.features.product.dto.request.ProductoRequestDto;
import com.titta.api.features.product.dto.response.ProductoResponseDto;
import com.titta.api.config.exception.DuplicateResourceException;
import com.titta.api.config.exception.ResourceNotFoundException;
import com.titta.api.domain.model.Producto;
import com.titta.api.domain.repository.ProductoRepository;
import com.titta.api.features.product.service.ProductoService;
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
    private CategoriaRepository categoriaRepository;

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

    @Override
    @Transactional
    public ProductoResponseDto actualizarDetalles(Long idProducto, ProductoUpdateDetailsRequestDto detailsDto) {
        // 1. Encontrar el producto
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + idProducto));

        // 2. Validar SKU (si ha cambiado)
        if (!producto.getSku().equals(detailsDto.sku())) {
            if (productoRepository.existsBySku(detailsDto.sku())) {
                throw new DuplicateResourceException("Ya existe un producto con el SKU '" + detailsDto.sku() + "'.");
            }
            producto.setSku(detailsDto.sku());
        }

        // 3. Encontrar la Categoría
        Categoria categoria = categoriaRepository.findById(detailsDto.idCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el ID: " + detailsDto.idCategoria()));

        // 4. Actualizar los campos
        producto.setNombreProducto(detailsDto.nombreProducto());
        producto.setDescripcion(detailsDto.descripcion());
        producto.setPrecio(detailsDto.precio());
        producto.setCategoria(categoria);

        // 5. Guardar y retornar
        Producto productoActualizado = productoRepository.save(producto);
        return productoMapper.toResponseDto(productoActualizado);
    }

    @Override
    @Transactional
    public void cambiarEstado(Long idProducto, boolean nuevoEstado) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + idProducto));

        producto.setEstadoProducto(nuevoEstado);
        productoRepository.save(producto);
    }
}