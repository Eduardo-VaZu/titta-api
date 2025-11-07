package com.titta.api.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_direccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_direccion")
    private Long idDireccion;

    @Column(name = "calle")
    private String calle;

    @Column(name = "numero_exterior", length = 20)
    private String numeroExterior;

    @Column(name = "codigo_postal", length = 20)
    private String codigoPostal;

    @Column(name = "ciudad", nullable = false)
    private String ciudad;

    @Column(name = "estado_provincial", nullable = false)
    private String estadoProvincial;
}
