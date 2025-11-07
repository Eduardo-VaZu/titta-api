package com.titta.api.domain.repository;

import com.titta.api.domain.model.Rol;
import com.titta.api.domain.model.enums.RolEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    // Spring Data JPA creará la consulta automáticamente a partir del nombre del método.
    Optional<Rol> findByNombreRol(RolEnum nombreRol);
}
