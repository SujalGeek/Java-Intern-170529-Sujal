package com.example.user_service.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.LoginResponse;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.security.JwtService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;

    public LoginResponse login(LoginRequest request) {
    	System.out.println("Here going onto the service bro!!");
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        System.out.println("perfectly return user" +user);
        System.out.println("Returning the request: "+request);
        if (!user.getIsActive()) {
            throw new RuntimeException("User account is inactive");
        }

        //
        BCryptPasswordEncoder testEncoder = new BCryptPasswordEncoder();
        boolean matches = testEncoder.matches(request.getPassword(), user.getPassword());
        System.out.println("TEST ENCODER RESULT: " + matches);

        if (!matches) {
            throw new RuntimeException("Invalid username2 or password");
        }
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            throw new RuntimeException("Invalid username or password");
//        }

        System.out.println("Reached before token generation");
        String token = jwtService.generateToken(user.getUsername(), user.getUserId(), user.getRole());
        
        System.out.println("Here the token is generated "+token);
        return new LoginResponse(token, user.getUserId(), user.getUsername(), user.getRole(), user.getFullName(), "Login successful");
    }

    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }
    
    public void processForgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        user.setResetToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        // 🔥 REAL SHIPMENT: Sending via SendGrid API
        emailService.sendResetToken(email, token);
    }

    public void updatePassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Token"));

        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        // Encrypt the new password before saving!
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); // Clear token after use
        user.setTokenExpiry(null);
    }
}