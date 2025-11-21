package com.titta.api.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class DetalleVentaId implements Serializable {

    @Column(name = "id_venta")
    private Long idVenta;

    @Column(name = "id_producto")
    private Long idProducto;
}