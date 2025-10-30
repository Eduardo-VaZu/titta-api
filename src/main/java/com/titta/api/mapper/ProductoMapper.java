package com.titta.api.mapper;

import com.titta.api.dto.request.ProductoRequestDto;
import com.titta.api.dto.response.ProductoResponseDto;
import com.titta.api.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductoMapper {

    public Producto toProducto(ProductoRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }

        Producto producto = Producto.builder()
                .nombreProducto(requestDto.nombreProducto())
                .sku(requestDto.sku())
                .descripcion(requestDto.descripcion())
                .precio(requestDto.precio())
                .estadoProducto(requestDto.estadoProducto())
                .categoria(Categoria.builder()
                        .idCategoria(requestDto.idCategoria())
                        .build())
                .build();

        ImagenProducto imagenProducto = ImagenProducto.builder()
                .imagenUrl(requestDto.imagen().imagenUrl())
                .altText(requestDto.imagen().altText())
                .producto(producto)
                .build();
        producto.setImagen(imagenProducto);

        if (requestDto.stockSede() != null && !requestDto.stockSede().isEmpty()) {
            Set<StockSede> stocks = requestDto.stockSede().stream()
                    .map(stockDto -> StockSede.builder()
                            .id(StockSedeId.builder()
                                    .idSede(stockDto.idSede())
                                    .build())
                            .sede(Sede.builder().idSede(stockDto.idSede()).build())
                            .cantidad(stockDto.stock())
                            .producto(producto)
                            .build())
                    .collect(Collectors.toSet());
            producto.setStocks(stocks);
        }

        return producto;
    }

    public ProductoResponseDto toResponseDto(Producto producto) {
        if (producto == null) {
            return null;
        }

        ProductoResponseDto.CategoriaResponseDto categoriaDto = null;
        if (producto.getCategoria() != null) {
            categoriaDto = new ProductoResponseDto.CategoriaResponseDto(
                    producto.getCategoria().getIdCategoria(),
                    producto.getCategoria().getNombreCategoria()
            );
        }

        ProductoResponseDto.ImagenResponseDto imagenDto = null;
        if (producto.getImagen() != null) {
            imagenDto = new ProductoResponseDto.ImagenResponseDto(
                    producto.getImagen().getIdImagen(),
                    producto.getImagen().getImagenUrl(),
                    producto.getImagen().getAltText()
            );
        }

        List<ProductoResponseDto.StockSedeResponseDto> stockSedeList = null;
        if (producto.getStocks() != null && !producto.getStocks().isEmpty()) {
            stockSedeList = producto.getStocks().stream().map(stockSede ->
                    new ProductoResponseDto.StockSedeResponseDto(
                            stockSede.getSede().getIdSede(),
                            stockSede.getSede().getNombreSede(),
                            stockSede.getCantidad()
                    )
            ).collect(Collectors.toList());
        }

        return new ProductoResponseDto(
                producto.getNombreProducto(),
                producto.getSku(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getEstadoProducto(),
                categoriaDto,
                imagenDto,
                stockSedeList
        );
    }

}
