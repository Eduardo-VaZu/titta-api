package com.titta.api.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_stock_sede")
@Getter
@Setter
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

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
