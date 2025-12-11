package com.example.user_service.service;

import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
// @RequiredArgsConstructor  <-- REMOVE THIS
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    // --- MANUAL CONSTRUCTOR (The Fix) ---
    // This tells Spring: "When you build UserService, pass me the UserRepository"
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Ensure Spring `GrantedAuthority` has ROLE_ prefix
        String roleStr = user.getRole() != null ? user.getRole().name() : "STUDENT";
        if (!roleStr.startsWith("ROLE_")) roleStr = "ROLE_" + roleStr;

        var authority = new SimpleGrantedAuthority(roleStr);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword() == null ? "" : user.getPassword(),
                List.of(authority)
        );
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}