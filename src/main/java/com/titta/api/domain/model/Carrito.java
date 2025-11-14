package com.titta.api.domain.model;

import com.titta.api.domain.model.base.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_carrito")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrito extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

//    @Column(name = "fecha_creacion", nullable = false)
//    private LocalDateTime fechaCreacion;

//    @Column(name = "fecha_actualizacion", nullable = false)
//    private LocalDateTime fechaActualizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sede")
    @EqualsAndHashCode.Exclude
    private Sede sede;

    @Column(name = "estado", nullable = false, length = 50)
    private String estado;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private Set<ItemCarrito> items = new HashSet<>();
}
