package com.titta.api.domain.repository;

import com.titta.api.domain.model.Carrito;
import com.titta.api.domain.model.enums.EstadoCarritoEnum;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Carrito, Long> {

    @EntityGraph(attributePaths = {"items.producto.imagen", "sede"})
    Optional<Carrito> findByUsuario_IdUsuarioAndEstado(Long idUsuario, EstadoCarritoEnum estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"items.producto.imagen", "sede"})
    @Query("""
            SELECT c
            FROM Carrito c
            WHERE c.usuario.idUsuario = :idUsuario AND c.estado = :estado
            """)
    Optional<Carrito> findByUsuarioIdAndEstadoForUpdate(@Param("idUsuario") Long idUsuario,
                                                         @Param("estado") EstadoCarritoEnum estado);

    boolean existsBySede_IdSedeAndEstado(Long idSede, EstadoCarritoEnum estado);

    @EntityGraph(attributePaths = {"items.producto"})
    List<Carrito> findAllByEstado(EstadoCarritoEnum estado);

}
