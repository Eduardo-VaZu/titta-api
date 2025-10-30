package com.titta.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tbl_detalle_venta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleVenta {

    @EmbeddedId
    private DetalleVentaId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idVenta")
    @JoinColumn(name = "id_venta")
    @EqualsAndHashCode.Exclude
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idProducto")
    @JoinColumn(name = "id_producto")
    @EqualsAndHashCode.Exclude
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
}

