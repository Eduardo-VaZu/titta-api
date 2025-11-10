package com.titta.api.features.product.mapper;

import com.titta.api.domain.model.*;
import com.titta.api.features.product.dto.request.ProductoRequestDto;
import com.titta.api.features.product.dto.response.ProductoResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductoMapper {

    public Producto toProducto(ProductoRequestDto dto, Categoria categoria, List<Sede> sedes) {
        if (dto == null) {
            return null;
        }

        Producto producto = Producto.builder()
                .nombreProducto(dto.nombreProducto())
                .sku(dto.sku())
                .descripcion(dto.descripcion())
                .precio(dto.precio())
                .estadoProducto(dto.estadoProducto())
                .categoria(categoria)
                .build();

        ImagenProducto imagen = ImagenProducto.builder()
                .imagenUrl(dto.imagen().imagenUrl())
                .altText(dto.imagen().altText())
                .producto(producto)
                .build();
        producto.setImagen(imagen);

        var sedesMap = sedes.stream()
                .collect(Collectors.toMap(Sede::getIdSede, sede -> sede));

        var stocksSet = dto.stocks().stream()
                .map(stockDto -> StockSede.builder()
                        .id(new StockSedeId(null, stockDto.idSede()))
                        .producto(producto)
                        .sede(sedesMap.get(stockDto.idSede()))
                        .cantidad(stockDto.cantidad())
                        .build())
                .collect(Collectors.toSet());

        producto.setStocks(stocksSet);

        return producto;
    }

    public ProductoResponseDto toProductoResponseDto(Producto producto) {
        if (producto == null) {
            return null;
        }

        ProductoResponseDto.ImagenResponseDto imagenDto = null;
        if (producto.getImagen() != null) {
            imagenDto = new ProductoResponseDto.ImagenResponseDto(
                    producto.getImagen().getIdImagen(),
                    producto.getImagen().getImagenUrl(),
                    producto.getImagen().getAltText()
            );
        }

        List<ProductoResponseDto.StockSedeResponseDto> stocksDto = producto.getStocks().stream()
                .map(stock -> new ProductoResponseDto.StockSedeResponseDto(
                        producto.getIdProducto(),
                        stock.getSede().getIdSede(),
                        stock.getCantidad()
                ))
                .collect(Collectors.toList());

        return new ProductoResponseDto(
                producto.getIdProducto(),
                producto.getNombreProducto(),
                producto.getSku(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getEstadoProducto(),
                producto.getCategoria().getIdCategoria(),
                imagenDto,
                stocksDto
        );
    }
}