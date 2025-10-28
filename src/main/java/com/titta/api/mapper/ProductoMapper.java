package com.titta.api.mapper;

import com.titta.api.dto.request.ImagenRequestDto;
import com.titta.api.dto.request.ProductoRequestDto;
import com.titta.api.dto.response.ProductoResponseDto;
import com.titta.api.model.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
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
        imagenProducto.setProducto(producto);
        producto.setImagen(imagenProducto);

        if (requestDto.getStockSede() != null && !requestDto.getStockSede().isEmpty()) {
            producto.setStocks(requestDto.getStockSede().stream().map(stockDto -> {
                StockSede stockSede = new StockSede();
                Sede sede = new Sede();
                sede.setIdSede(stockDto.getIdSede());
                stockSede.setSede(sede);
                stockSede.setCantidad(stockDto.getStock());
                stockSede.setProducto(producto);
                return stockSede;
            }).collect(Collectors.toSet()));
        }

        return producto;
    }

    public ProductoResponseDto toResponseDto(Producto producto) {
        if (producto == null) {
            return null;
        }

        ProductoResponseDto responseDto = new ProductoResponseDto();
        responseDto.setNombreProducto(producto.getNombreProducto());
        responseDto.setSku(producto.getSku());
        responseDto.setDescripcion(producto.getDescripcion());
        responseDto.setPrecio(producto.getPrecio());
        responseDto.setEstadoProducto(producto.isEstadoProducto());

        if (producto.getCategoria() != null) {
            ProductoResponseDto.CategoriaResponseDto categoriaDto = new ProductoResponseDto.CategoriaResponseDto();
            categoriaDto.setIdCategoria(producto.getCategoria().getIdCategoria());
            categoriaDto.setNombreCategoria(producto.getCategoria().getNombreCategoria());
            responseDto.setCategoria(categoriaDto);
        }

        if (producto.getImagen() != null) {
            ProductoResponseDto.InagenResponseDto imagenDto = new ProductoResponseDto.InagenResponseDto();
            imagenDto.setIdImagen(producto.getImagen().getIdImagen());
            imagenDto.setImagenUrl(producto.getImagen().getImagenUrl());
            imagenDto.setAltText(null);
            responseDto.setImagenes(Collections.singletonList(imagenDto));
        }

        if (producto.getStocks() != null && !producto.getStocks().isEmpty()) {
            responseDto.setStockSede(producto.getStocks().stream().map(stockSede -> {
                ProductoResponseDto.StockSedeResponseDto stockDto = new ProductoResponseDto.StockSedeResponseDto();
                stockDto.setIdSede(stockSede.getSede().getIdSede());
                stockDto.setNombreSede(stockSede.getSede().getNombreSede());
                stockDto.setStock(stockSede.getCantidad());
                return stockDto;
            }).collect(Collectors.toList()));
        }

        return responseDto;
    }

}
