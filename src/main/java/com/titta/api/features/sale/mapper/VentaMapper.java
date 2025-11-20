package com.titta.api.features.sale.mapper;

import com.titta.api.domain.model.*;
import com.titta.api.features.sale.dto.response.SaleResponseDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;

@Component
public class VentaMapper {

    public SaleResponseDto toVentaResponseDto(Venta venta) {
        if (venta == null) {
            return null;
        }

        return new SaleResponseDto(
                venta.getId(),
                venta.getFechaVenta(),
                venta.getTotal(),
                venta.getEstadoVenta() != null ? venta.getEstadoVenta().getNombreEstado() : null,
                venta.getMetodoPago() != null ? venta.getMetodoPago().getNombreMetodo() : null,
                venta.getDetalles() != null ? venta.getDetalles().size() : 0
        );
    }

    public Venta toVentaEntity(Usuario usuario, Carrito carrito, MetodoPago metodoPago, EstadoVenta estadoVenta) {
        return Venta.builder()
                .usuario(usuario)
                .fechaVenta(LocalDateTime.now())
                .metodoPago(metodoPago)
                .estadoVenta(estadoVenta)
                .sede(carrito.getSede())
                .carrito(carrito)
                .total(BigDecimal.ZERO)
                .detalles(new HashSet<>())
                .build();
    }

    public DetalleVenta toDetalleVentaEntity(ItemCarrito item, Venta venta) {
        return DetalleVenta.builder()
                .id(new DetalleVentaId(null, item.getProducto().getIdProducto()))
                .venta(venta)
                .producto(item.getProducto())
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .build();
    }
}