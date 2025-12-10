package com.example.user_service.service;

import com.example.user_service.dto.AuthResponse;
import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.RegisterRequest;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.security.JwtUtil; // <--- CHANGED FROM JwtService
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor    
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
    // --- CHANGED to match your file name ---
    private final JwtUtil jwtUtil; 

    // --- 1. REGISTER ---
    public String register(RegisterRequest request) {
        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        
        // Verification Logic
        user.setVerified(false);
        String code = UUID.randomUUID().toString();
        user.setVerificationCode(code);

        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), code);

        return "Registration successful! Please check your email to verify.";
    }

    // --- 2. LOGIN ---
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid Credentials");
        }

        if (!user.isVerified()) {
            throw new RuntimeException("Account not verified. Please check your email.");
        }

        // --- Use jwtUtil here ---
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(
            token,
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole()
        );
    }

    // --- 3. VERIFY EMAIL ---
    public String verifyUser(String code) {
        User user = userRepository.findByVerificationCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid Token"));

        user.setVerified(true);
        user.setVerificationCode(null);
        userRepository.save(user);
        return "Account Verified Successfully!";
    }

    // --- 4. FORGOT PASSWORD ---
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        userRepository.save(user);
        
        emailService.sendResetToken(email, token);
        return "Reset link sent to your email.";
    }

    // --- 5. RESET PASSWORD ---
    public String resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Reset Token"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        userRepository.save(user);
        return "Password reset successfully.";
    }
}