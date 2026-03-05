package com.example.user_service.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.user_service.entity.RevokedToken;
import com.example.user_service.repository.RevokedTokenRepository;

@Service
public class TokenService {
    
	@Autowired
    private RevokedTokenRepository repo;

    public void blacklistToken(String token) {
        RevokedToken rt = new RevokedToken();
        rt.setToken(token);
        rt.setRevokedAt(LocalDateTime.now());
        repo.save(rt);
    }

    public boolean isBlacklisted(String token) {
        return repo.existsByToken(token);
    }
}