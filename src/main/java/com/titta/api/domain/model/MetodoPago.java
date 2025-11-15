package com.titta.api.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_metodo_pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metodo_pago")
    private Long idMetodoPago;

    @Column(name = "nombre_metodo", nullable = false, unique = true, length = 50)
    private String nombreMetodo;
}