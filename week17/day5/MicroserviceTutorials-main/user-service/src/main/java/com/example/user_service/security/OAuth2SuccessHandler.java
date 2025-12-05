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

    // Direct link to the Dashboard since your React code handles the parsing there
    private final String FRONTEND_URL = "http://localhost:5173/auth/callback";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        // 1. Create or Get User
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User u = new User();
            u.setEmail(email);
            u.setFullName(name != null ? name : email);
            u.setGoogleUser(true);
            u.setRole(Role.STUDENT); // Default role
            return userRepository.save(u);
        });

        // 2. Generate JWT
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        // 3. Construct Redirect URL with Token AND Role
        // We use URLEncoder to ensure special characters don't break the URL
        String redirectUrl = FRONTEND_URL 
                + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)
                + "&role=" + user.getRole().name();

        // 4. Send ONE Redirect
        response.sendRedirect(redirectUrl);
    }
}