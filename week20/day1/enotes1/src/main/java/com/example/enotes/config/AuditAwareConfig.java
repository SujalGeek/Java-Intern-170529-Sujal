package com.example.enotes.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration // 1. Add this
public class AuditAwareConfig {

    @Bean("auditAware") // 2. Add this to register the bean name used in Main class
    public AuditorAware<Integer> auditorAware() {
        return new AuditorAwareImpl();
    }
    
    // Inner class logic
    public static class AuditorAwareImpl implements AuditorAware<Integer> {
        @Override
        public Optional<Integer> getCurrentAuditor() {
            return Optional.of(2); // Hardcoded for now
        }
    }
}