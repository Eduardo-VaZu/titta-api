package com.titta.api.domain.repository;

import com.titta.api.domain.model.DetalleVenta;
import com.titta.api.features.report.dto.ProductoMasVendidoDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    @Query("""
            SELECT new com.titta.api.features.report.dto.ProductoMasVendidoDto(
                d.producto.idProducto,
                d.producto.nombreProducto,
                d.producto.sku,
                SUM(d.cantidad)
            )
            FROM DetalleVenta d
            GROUP BY d.producto.idProducto, d.producto.nombreProducto, d.producto.sku
            ORDER BY SUM(d.cantidad) DESC
            """)
    List<ProductoMasVendidoDto> findProductosMasVendidos(Pageable pageable);
}