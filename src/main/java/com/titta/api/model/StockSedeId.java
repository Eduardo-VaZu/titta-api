package com.titta.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockSedeId implements Serializable {

    @Column(name = "id_producto")
    private Long idProducto;

    @Column(name = "id_sede")
    private Long idSede;

}
