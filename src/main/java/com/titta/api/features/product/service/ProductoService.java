package com.titta.api.features.product.service;

import com.titta.api.features.product.dto.request.ProductoBatchRequestDto;
import com.titta.api.features.product.dto.request.ProductoRequestDto;
import com.titta.api.features.product.dto.request.ProductoUpdateDto;
import com.titta.api.features.product.dto.response.ProductoResponseDto;

import java.util.List;

public interface ProductoService {

    ProductoResponseDto crearProducto(ProductoRequestDto requestDto);
    //List<ProductoResponseDto> crearProductosBatch(ProductoBatchRequestDto requestDto);
    ProductoResponseDto getProductoById(Long idProducto);
    List<ProductoResponseDto> getAllProductos(Boolean estado);
    ProductoResponseDto updateProducto(Long idProducto, ProductoUpdateDto updateDto);
    void deleteProducto(Long idProducto);
}