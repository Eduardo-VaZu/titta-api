package com.titta.api.domain.repository;

import com.titta.api.domain.model.Carrito;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Carrito, Long> {

    @EntityGraph(attributePaths = {"items.producto.imagen", "sede"})
    Optional<Carrito> findByUsuario_IdUsuarioAndEstado(Long idUsuario, String estado);

}