package com.titta.api.mapper;

import com.titta.api.dto.request.ProductoRequestDto;
import com.titta.api.dto.response.ProductoResponseDto;
import com.titta.api.model.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProductoMapper {

    public Producto toProducto(ProductoRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }

        Producto producto = new Producto();
        producto.setNombreProducto(requestDto.nombreProducto());
        producto.setSku(requestDto.sku());
        producto.setDescripcion(requestDto.descripcion());
        producto.setPrecio(requestDto.precio());
        producto.setEstadoProducto(requestDto.estadoProducto());

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(requestDto.idCategoria());
        producto.setCategoria(categoria);

        ImagenProducto imagenProducto = new ImagenProducto();
        imagenProducto.setImagenUrl(requestDto.imagen().imagenUrl());
        imagenProducto.setAltText(requestDto.imagen().altText());
        imagenProducto.setProducto(producto);
        producto.setImagen(imagenProducto);

        if (requestDto.stockSede() != null && !requestDto.stockSede().isEmpty()) {
            producto.setStocks(requestDto.stockSede().stream().map(stockDto -> {
                StockSede stockSede = new StockSede();
                Sede sede = new Sede();
                sede.setIdSede(stockDto.idSede());
                stockSede.setSede(sede);
                stockSede.setCantidad(stockDto.stock());
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

        ProductoResponseDto responseDto = new ProductoResponseDto(
                producto.getNombreProducto(),
                producto.getSku(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getEstadoProducto(),

        );
        
        if (producto.getCategoria() != null) {
            ProductoResponseDto.CategoriaResponseDto categoriaDto = new ProductoResponseDto.CategoriaResponseDto();
            categoriaDto.setIdCategoria(producto.getCategoria().getIdCategoria());
            categoriaDto.setNombreCategoria(producto.getCategoria().getNombreCategoria());
            responseDto.setCategoria(categoriaDto);
        }

        if (producto.getImagen() != null) {
            ProductoResponseDto.ImagenResponseDto imagenDto = new ProductoResponseDto.ImagenResponseDto();
            imagenDto.setIdImagen(producto.getImagen().getIdImagen());
            imagenDto.setImagenUrl(producto.getImagen().getImagenUrl());
            imagenDto.setAltText(producto.getImagen().getAltText());
            responseDto.setImagenes(imagenDto);
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
