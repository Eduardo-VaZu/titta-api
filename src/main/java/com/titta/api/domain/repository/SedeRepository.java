package com.titta.api.domain.repository;

import com.titta.api.domain.model.Sede;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SedeRepository extends JpaRepository<Sede, Long> {
    boolean existsByNombreSede(String nombreSede);
}