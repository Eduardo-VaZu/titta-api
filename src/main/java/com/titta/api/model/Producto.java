package com.titta.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @Column(name = "nombre_producto", nullable = false, length = 100)
    private String nombreProducto;

    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "estado_producto", nullable = false, length = 5)
    private Boolean estadoProducto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    @EqualsAndHashCode.Exclude
    private Categoria categoria;

    @OneToOne(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private ImagenProducto imagen;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<StockSede> stocks = new HashSet<>();
}