package com.titta.api.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_usuario_sede")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioSede {

    @EmbeddedId
    private UsuarioSedeId Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario")
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idSede")
    @JoinColumn(name = "id_sede")
    @EqualsAndHashCode.Exclude
    private Sede sede;

    @Column(name = "rol_en_sede", length = 50)
    private String rolEnSede;
}

