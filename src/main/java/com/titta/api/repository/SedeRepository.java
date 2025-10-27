package com.titta.api.repository;

import com.titta.api.model.Sede;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SedeRepository extends JpaRepository<Sede, Long> {
    boolean existsByNombreSede(String nombreSede);
}