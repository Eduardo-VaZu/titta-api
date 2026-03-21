package com.titta.api.domain.repository;

import com.titta.api.domain.model.StockSede;
import com.titta.api.domain.model.StockSedeId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockSedeRepository extends JpaRepository<StockSede, StockSedeId> {
    @EntityGraph(attributePaths = {"sede", "producto"})
    List<StockSede> findAllById_IdSede(Long idSede);

    @EntityGraph(attributePaths = {"sede", "producto"})
    List<StockSede> findByCantidadLessThan(int umbral);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT s
            FROM StockSede s
            WHERE s.id.idProducto = :idProducto AND s.id.idSede = :idSede
            """)
    Optional<StockSede> findByIdForUpdate(@Param("idProducto") Long idProducto, @Param("idSede") Long idSede);
}
