package com.titta.api.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class StockSedeId implements Serializable {

    @Column(name = "id_producto")
    private Long idProducto;

    @Column(name = "id_sede")
    private Long idSede;

}
