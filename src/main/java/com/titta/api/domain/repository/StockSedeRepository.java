package com.titta.api.domain.repository;

import com.titta.api.domain.model.StockSede;
import com.titta.api.domain.model.StockSedeId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockSedeRepository extends JpaRepository<StockSede, StockSedeId> {
    @EntityGraph(attributePaths = {"sede", "producto"})
    List<StockSede> findAllById_IdSede(Long idSede);
}
