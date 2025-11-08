package com.titta.api.features.product.mapper;

import com.titta.api.domain.model.*;
import com.titta.api.features.product.dto.request.ProductoRequestDto;
import com.titta.api.features.product.dto.response.ProductoResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

    @Component
    public class ProductoMapper {

        // --- **CAMBIO 1: FIRMA DEL MÉTODO MODIFICADA** ---
        public Producto toProducto(ProductoRequestDto requestDto, Categoria categoria, List<Sede> sedes) {
            if (requestDto == null) {
                return null;
            }

            Producto producto = Producto.builder()
                    .nombreProducto(requestDto.nombreProducto())
                    .sku(requestDto.sku())
                    .descripcion(requestDto.descripcion())
                    .precio(requestDto.precio())
                    .estadoProducto(requestDto.estadoProducto())
                    .categoria(categoria) // <-- **CAMBIO 2: Usar la entidad Categoria real**
                    .build();

            ImagenProducto imagenProducto = ImagenProducto.builder()
                    .imagenUrl(requestDto.imagen().imagenUrl())
                    .altText(requestDto.imagen().altText())
                    .producto(producto)
                    .build();
            producto.setImagen(imagenProducto);

            // --- **CAMBIO 3: LÓGICA DE STOCK MODIFICADA** ---
            if (requestDto.stockSede() != null && !requestDto.stockSede().isEmpty()) {
                Set<StockSede> stocks = requestDto.stockSede().stream()
                        .map(stockDto -> {
                            // Encontrar la entidad Sede manejada correspondiente al DTO
                            Sede sede = sedes.stream()
                                    .filter(s -> s.getIdSede().equals(stockDto.idSede()))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalStateException("Sede no encontrada en la lista pre-cargada: " + stockDto.idSede())); // Seguridad

                            // **ARREGLO DEL BUG DE @MapsId**: Se elimina el .id(...)
                            return StockSede.builder()
                                    .sede(sede) // <-- Usar la entidad Sede real
                                    .cantidad(stockDto.stock())
                                    .producto(producto)
                                    .build();
                        })
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
                                // **POSIBLE MEJORA (N+1)**: Asegurarse que el nombreSede no esté nulo
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
