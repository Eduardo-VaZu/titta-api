package com.titta.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_imagen_producto")
@Data
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

    @Column(name = "alt_text", nullable = false)
    private String altText;

}