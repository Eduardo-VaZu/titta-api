package com.titta.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_stock_sede")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockSede {

    @EmbeddedId
    private StockSedeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idProducto")
    @JoinColumn(name = "id_producto")
    @EqualsAndHashCode.Exclude
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idSede")
    @JoinColumn(name = "id_sede")
    @EqualsAndHashCode.Exclude
    private Sede sede;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    
}
