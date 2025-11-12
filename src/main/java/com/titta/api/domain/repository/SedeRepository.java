package com.titta.api.domain.repository;

import com.titta.api.domain.model.Sede;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SedeRepository extends JpaRepository<Sede, Long> {
    boolean existsByNombreSede(String nombreSede);

    Optional<Sede> findByNombreSede(String nombreSede);

    @EntityGraph(attributePaths = {"direccion", "horariosOperacion"})
    @Override
    Page<Sede> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"direccion", "horariosOperacion"})
    Page<Sede> findByEstado(Boolean estado, Pageable pageable);

    @EntityGraph(attributePaths = {"direccion", "horariosOperacion"})
    @Override
    Optional<Sede> findById(Long idSede);
}