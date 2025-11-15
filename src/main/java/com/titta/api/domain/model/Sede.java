package com.titta.api.domain.model;

import com.titta.api.domain.model.base.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tbl_sede")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sede extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sede")
    private Long idSede;

    @Column(name = "nombre_sede", nullable = false, unique = true, length = 100)
    private String nombreSede;

    @Column(name = "telefono", unique = true, length = 20)
    private String telefono;

    @Column(name = "estado", nullable = false)
    private Boolean estado;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_direccion", referencedColumnName = "id_direccion", nullable = false)
    private Direccion direccion;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "sede", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HorarioOperacionSede> horariosOperacion = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "sede", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StockSede> stocks = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "sede", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UsuarioSede> usuariosSedes = new HashSet<>();


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Sede sede = (Sede) o;
        return Objects.equals(idSede, sede.idSede);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idSede);
    }
}
