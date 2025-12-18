package com.example.enotes.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

public class AuditAwareConfig implements AuditorAware<Integer>{

	@Override
	public Optional<Integer> getCurrentAuditor() {
		// TODO Auto-generated method stub
		return Optional.of(2);
	}

}
