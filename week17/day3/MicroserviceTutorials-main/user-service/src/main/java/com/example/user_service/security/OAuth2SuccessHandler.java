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

    // frontend redirect base — change to your deployed frontend URL when deployed
    private final String FRONTEND_REDIRECT = "http://localhost:5173/oauth/success";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        // create or get user
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User u = new User();
            u.setEmail(email);
            u.setFullName(name != null ? name : email);
            u.setGoogleUser(true);
            u.setRole(Role.STUDENT); // default role
            return userRepository.save(u);
        });

        // generate JWT
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        // redirect to frontend with token as query param (URL-encode token)
        String targetUrl = FRONTEND_REDIRECT + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);

        response.sendRedirect(targetUrl);
    }
}
