package com.titta.api.domain.model;


import com.titta.api.domain.model.enums.RolEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_rol")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @Enumerated(EnumType.STRING)
    @Column(name = "nombre_rol", nullable = false, unique = true, length = 50)
    private RolEnum nombreRol;

}
