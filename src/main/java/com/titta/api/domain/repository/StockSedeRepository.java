package com.titta.api.domain.repository;

import com.titta.api.domain.model.StockSede;
import com.titta.api.domain.model.StockSedeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockSedeRepository extends JpaRepository<StockSede, StockSedeId> {
}
