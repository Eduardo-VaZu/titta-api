package com.titta.api.domain.repository;

import com.titta.api.domain.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsByNombreCategoria(String nombre);

    Optional<Categoria> findByNombreCategoria(String nombre);

    List<Categoria> findAllByEstado(Boolean estado);
}
