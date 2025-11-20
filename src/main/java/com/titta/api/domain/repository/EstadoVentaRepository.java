package com.titta.api.domain.repository;

import com.titta.api.domain.model.EstadoVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoVentaRepository extends JpaRepository<EstadoVenta, Long> {
    Optional<EstadoVenta> findByNombreEstado(String nombreEstado);
}