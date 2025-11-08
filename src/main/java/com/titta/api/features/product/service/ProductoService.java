package com.titta.api.features.product.service;

import com.titta.api.features.product.dto.request.ProductoRequestDto;
import com.titta.api.features.product.dto.request.ProductoUpdateDetailsRequestDto;
import com.titta.api.features.product.dto.response.ProductoResponseDto;

import java.util.List;

public interface ProductoService {
    ProductoResponseDto crearProducto(ProductoRequestDto productoRequestDto);
    List<ProductoResponseDto> obtenerTodosLosProductos();
    ProductoResponseDto obtenerProductoPorId(Long id);
    ProductoResponseDto actualizarDetalles(Long idProducto, ProductoUpdateDetailsRequestDto detailsDto);
    void cambiarEstado(Long idProducto, boolean nuevoEstado);
}
