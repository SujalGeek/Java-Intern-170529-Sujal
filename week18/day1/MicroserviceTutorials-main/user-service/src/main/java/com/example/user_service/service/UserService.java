package com.example.user_service.service;

import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Ensure Spring `GrantedAuthority` has ROLE_ prefix
        String roleStr = user.getRole() != null ? user.getRole().name() : "STUDENT";
        if (!roleStr.startsWith("ROLE_")) roleStr = "ROLE_" + roleStr;

        var authority = new SimpleGrantedAuthority(roleStr);

        // If password is null (google user), we still return a UserDetails object;
        // Spring Security won't allow username/password auth but token-based works.
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
