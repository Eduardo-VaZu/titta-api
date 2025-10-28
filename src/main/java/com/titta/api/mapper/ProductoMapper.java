package com.titta.api.mapper;

import com.titta.api.dto.request.ImagenRequestDto;
import com.titta.api.dto.request.ProductoRequestDto;
import com.titta.api.dto.response.ProductoResponseDto;
import com.titta.api.model.Categoria;
import com.titta.api.model.ImagenProducto;
import com.titta.api.model.Producto;
import com.titta.api.model.StockSede;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
public class ProductoMapper {

    public Producto toProducto(ProductoRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }

        Producto producto = new Producto();
        producto.setNombreProducto(requestDto.getNombreProducto());
        producto.setSku(requestDto.getSku());
        producto.setDescripcion(requestDto.getDescripcion());
        producto.setPrecio(requestDto.getPrecio());
        producto.setEstadoProducto(requestDto.isEstadoProducto());

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(requestDto.getIdCategoria());
        producto.setCategoria(categoria);

        ImagenProducto imagenProducto = new ImagenProducto();
        imagenProducto.setImagenUrl(requestDto.getImagen().getImagenUrl());
        producto.setImagen(imagenProducto);

        return producto;
    }

    public ProductoResponseDto toResponseDto(Producto producto) {
        if (producto == null) {
            return null;
        }
        return null;
    }

}
