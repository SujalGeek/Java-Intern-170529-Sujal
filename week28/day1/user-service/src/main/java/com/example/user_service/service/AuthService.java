package com.example.user_service.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.LoginResponse;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.security.JwtService;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!user.getIsActive()) {
            throw new RuntimeException("User account is inactive");
        }

        //
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getUserId(), user.getRole());
        return new LoginResponse(token, user.getUserId(), user.getUsername(), user.getRole(), user.getFullName(), "Login successful");
    }

    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }
}