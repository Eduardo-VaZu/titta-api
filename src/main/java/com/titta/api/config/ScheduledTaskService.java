package com.titta.api.config;

import com.titta.api.domain.repository.TokenBlacklistRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
public class ScheduledTaskService {

    @Autowired
    private TokenBlacklistRepository tokenBlacklistRepository;

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void purgeExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Iniciando tarea programada: Limpieza de tokens en blacklist expirados antes de {}", now);

        try {
            tokenBlacklistRepository.deleteAllByFechaExpiracionBefore(now);
            log.info("Limpieza de tokens finalizada exitosamente.");
        } catch (Exception e) {
            log.error("Error durante la limpieza programada de tokens", e);
        }
    }
}