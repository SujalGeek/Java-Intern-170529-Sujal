package com.becoder.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

public class AuditorConfig implements AuditorAware<Integer> {

	@Override
	public Optional<Integer> getCurrentAuditor() {
		return Optional.of(2);
	}

}
