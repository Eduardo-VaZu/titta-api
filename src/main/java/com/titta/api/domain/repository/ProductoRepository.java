package com.titta.api.domain.repository;

import com.titta.api.domain.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsBySku(String sku);

    List<Producto> findBySkuIn(Collection<String> skus);

    @EntityGraph(attributePaths = {"categoria", "imagen", "stocks.sede"})
    Page<Producto> findByEstadoProducto(boolean estadoProducto, Pageable pageable);

    @EntityGraph(attributePaths = {"categoria", "imagen", "stocks.sede"})
    @Override
    List<Producto> findAll();
}
