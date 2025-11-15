package com.titta.api.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_imagen_producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen")
    private Long idImagen;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    @EqualsAndHashCode.Exclude
    private Producto producto;

    @Column(name = "imagen_url", nullable = false)
    private String imagenUrl;

    @Column(name = "alt_text")
    private String altText;

}