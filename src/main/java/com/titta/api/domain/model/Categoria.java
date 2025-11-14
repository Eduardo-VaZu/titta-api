package com.titta.api.domain.model;

import com.titta.api.domain.model.base.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tbl_categoria")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Categoria extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;

    @Column(name = "nombre_categoria", nullable = false, unique = true, length = 50)
    private String nombreCategoria;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private List<Producto> productos;
}