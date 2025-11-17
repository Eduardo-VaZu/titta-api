package com.titta.api.domain.repository;

import com.titta.api.domain.model.Carrito;
import com.titta.api.domain.model.enums.EstadoCarritoEnum;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Carrito, Long> {

    @EntityGraph(attributePaths = {"items.producto.imagen", "sede"})
    Optional<Carrito> findByUsuario_IdUsuarioAndEstado(Long idUsuario, EstadoCarritoEnum estado);

    boolean existsBySede_IdSedeAndEstado(Long idSede, EstadoCarritoEnum estado);

    @EntityGraph(attributePaths = {"items.producto"})
    List<Carrito> findAllByEstado(EstadoCarritoEnum estado);

}