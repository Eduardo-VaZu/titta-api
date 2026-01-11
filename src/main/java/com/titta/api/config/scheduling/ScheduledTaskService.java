package com.titta.api.config.scheduling;

import com.titta.api.domain.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTaskService {

    private final TokenBlacklistRepository tokenBlacklistRepository;

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
