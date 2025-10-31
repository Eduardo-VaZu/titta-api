package com.titta.api.repository;

import com.titta.api.model.Rol;
import com.titta.api.model.enums.RolEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    // Spring Data JPA creará la consulta automáticamente a partir del nombre del método.
    Optional<Rol> findByNombreRol(RolEnum nombreRol);
}
