package com.titta.api.features.product;

import com.titta.api.features.product.dto.ProductoRequestDto;
import com.titta.api.features.product.dto.ProductoResponseDto;

import java.util.List;

public interface ProductoService {
    ProductoResponseDto crearProducto(ProductoRequestDto productoRequestDto);
    List<ProductoResponseDto> obtenerTodosLosProductos();
    ProductoResponseDto obtenerProductoPorId(Long id);
}
