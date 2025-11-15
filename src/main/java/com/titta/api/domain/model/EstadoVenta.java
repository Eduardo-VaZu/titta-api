package com.titta.api.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_estado_venta")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EstadoVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_venta")
    private Long idEstadoVenta;

    @Column(name = "nombre_estado", nullable = false, unique = true, length = 50)
    private String nombreEstado;
}