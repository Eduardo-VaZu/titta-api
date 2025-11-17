package com.titta.api.features.cart.mapper;

import com.titta.api.domain.model.Carrito;
import com.titta.api.domain.model.ItemCarrito;
import com.titta.api.features.cart.dto.response.CartResponseDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    public CartResponseDto toCartResponseDto(Carrito carrito) {
        if (carrito == null) {
            return null;
        }

        List<CartResponseDto.ItemCarritoResponseDto> itemsDto = carrito.getItems().stream()
                .map(this::toItemCarritoResponseDto)
                .collect(Collectors.toList());

        BigDecimal subTotal = itemsDto.stream()
                .map(CartResponseDto.ItemCarritoResponseDto::subTotalItem)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = itemsDto.stream()
                .mapToInt(CartResponseDto.ItemCarritoResponseDto::cantidad)
                .sum();

        return new CartResponseDto(
                carrito.getId(),
                carrito.getSede() != null ? carrito.getSede().getIdSede() : null,
                carrito.getSede() != null ? carrito.getSede().getNombreSede() : null,
                carrito.getEstado().name(),
                itemsDto,
                subTotal,
                totalItems
        );
    }

    private CartResponseDto.ItemCarritoResponseDto toItemCarritoResponseDto(ItemCarrito item) {
        if (item == null) {
            return null;
        }

        BigDecimal precioUnitario = item.getPrecioUnitario();
        int cantidad = item.getCantidad();
        BigDecimal subTotalItem = precioUnitario.multiply(new BigDecimal(cantidad));

        String imagenUrl = null;
        if (item.getProducto() != null && item.getProducto().getImagen() != null) {
            imagenUrl = item.getProducto().getImagen().getImagenUrl();
        }

        return new CartResponseDto.ItemCarritoResponseDto(
                item.getProducto().getIdProducto(),
                item.getProducto().getNombreProducto(),
                item.getProducto().getSku(),
                imagenUrl,
                precioUnitario,
                cantidad,
                subTotalItem
        );
    }
}