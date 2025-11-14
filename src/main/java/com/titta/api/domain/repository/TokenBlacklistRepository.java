package com.titta.api.domain.repository;

import com.titta.api.domain.model.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, String> {
    void deleteAllByFechaExpiracionBefore(LocalDateTime now);
}
