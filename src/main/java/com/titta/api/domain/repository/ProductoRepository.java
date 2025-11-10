package com.titta.api.domain.repository;

import com.titta.api.domain.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsBySku(String sku);
    List<Producto> findByEstadoProducto(Boolean estado);
    List<Producto> findBySkuIn(List<String> skus);
}
