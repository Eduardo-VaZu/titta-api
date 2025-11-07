package com.titta.api.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleVentaId implements Serializable {

    @Column(name = "id_venta")
    private Long idVenta;

    @Column(name = "id_producto")
    private Long idProducto;
}