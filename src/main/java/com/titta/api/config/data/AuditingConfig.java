package com.titta.api.config.data;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
public class AuditingConfig {

    @Bean
    @SuppressWarnings("null")
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("SISTEMA");
            }

            String username = authentication.getName();
            if ("anonymousUser".equals(username)) {
                return Optional.of("SISTEMA_ANONIMO");
            }
            return Optional.of(username);
        };
    }
}
