package com.example.user_service.security;

import com.example.user_service.entity.Role;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // Matches your Frontend Route
    private final String FRONTEND_URL = "http://localhost:5173/auth/callback";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        // 1. Fetch Existing OR Create New User
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User u = new User();
            u.setEmail(email);
            u.setFullName(name != null ? name : email);
            u.setGoogleUser(true);
            u.setVerified(true); // Google accounts are implicitly verified
            u.setRole(Role.STUDENT); // Default role for NEW users
            return userRepository.save(u);
        });

        //  // 2. --- SELF-HEALING FIX FOR EXISTING USERS ---
        // // If an existing user (like ID 23) has a NULL role, fix it immediately.
        if (user.getRole() == null) {
            user.setRole(Role.STUDENT);
            user = userRepository.save(user); // Save the fix to DB
        }
        // ----------------------------------------------

        // 3. Generate Token (Now Safe!)
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        // 4. Redirect to Frontend
        String redirectUrl = FRONTEND_URL
                + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)
                + "&role=" + URLEncoder.encode(user.getRole().name(), StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl);
    }
}