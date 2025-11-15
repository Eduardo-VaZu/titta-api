package com.titta.api.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_credencial_tradicional")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CredencialTradicional {

    @Id
    @Column(name = "id_usuario")
    private Long idUsuario;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_usuario")
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
}
