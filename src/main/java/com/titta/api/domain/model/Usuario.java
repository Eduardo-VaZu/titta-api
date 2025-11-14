package com.titta.api.domain.model;

import com.titta.api.domain.model.base.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido_paterno", nullable = false, length = 100)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", nullable = false, length = 100)
    private String apellidoMaterno;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

//    @CreationTimestamp
//    @Column(name = "fecha_registro", nullable = false)
//    private LocalDate fechaRegistro;

    @Column(name = "estado_usuario", nullable = false)
    private boolean estadoUsuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private List<UsuarioSede> usuarioSedes = new ArrayList<>();

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private CredencialTradicional credencialTradicional;
}
