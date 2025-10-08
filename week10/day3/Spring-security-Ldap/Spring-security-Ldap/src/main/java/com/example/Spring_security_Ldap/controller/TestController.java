package com.example.Spring_security_Ldap.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

/**
 * REST Controller to test the security configuration.
 */
@RestController
public class TestController {

    /**
     * Public endpoint, accessible to everyone (Permitted by SpringConfig).
     */
    @GetMapping("/")
    public String home() {
        return "<h1>Welcome! This is the public home page.</h1>" +
                "<p>Try accessing the secured endpoint: <a href='/secured'>/secured</a></p>";
    }

    /**
     * Secured endpoint, only accessible after successful LDAP authentication.
     */
    @GetMapping("/secured")
    public String secured(Authentication authentication) {

        // Retrieve the authenticated user's name (uid)
        String username = authentication.getName();

        // Retrieve the user's authorities (roles/groups from LDAP)
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));

        return "<h1>Welcome, " + username + "!</h1>" +
                "<h2>You have successfully logged in via LDAP.</h2>" +
                "<p><strong>Your Roles (Groups):</strong> " + roles + "</p>";
    }
}
