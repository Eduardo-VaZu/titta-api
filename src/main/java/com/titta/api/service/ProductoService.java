package com.titta.api.service;

import com.titta.api.dto.request.ProductoRequestDto;
import com.titta.api.dto.response.ProductoResponseDto;

import java.util.List;

public interface ProductoService {
    ProductoResponseDto crearProducto(ProductoRequestDto productoRequestDto);
    List<ProductoResponseDto> obtenerTodosLosProductos();
    ProductoResponseDto obtenerProductoPorId(Long id);
}
