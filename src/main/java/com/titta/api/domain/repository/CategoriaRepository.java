package com.titta.api.domain.repository;

import com.titta.api.domain.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsByNombreCategoria(String nombre);
}
