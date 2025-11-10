package com.titta.api.domain.repository;

import com.titta.api.domain.model.Producto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsBySku(String sku);

    @EntityGraph(attributePaths = {"categoria", "imagen", "stocks.sede"})
    List<Producto> findByEstadoProducto(boolean estadoProducto);

    @EntityGraph(attributePaths = {"categoria", "imagen", "stocks.sede"})
    @Override
    List<Producto> findAll();
}
