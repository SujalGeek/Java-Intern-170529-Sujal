package com.example.user_service.security;

import com.example.user_service.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        String email;
        try {
            email = jwtUtil.extractUsername(jwt);
        } catch (Exception ex) {
            // invalid token — skip authentication and continue
            filterChain.doFilter(request, response);
            return;
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Try loading userDetails (if present in DB) — safer approach
            UserDetails userDetails = this.userService.loadUserByUsername(email);

            // validate token against loaded user
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // Prefer authorities from userDetails if present
                var authorities = userDetails.getAuthorities();
                if (authorities == null || authorities.isEmpty()) {
                    // Fallback: read role from token claim and create authority
                    String roleClaim = jwtUtil.extractRole(jwt);
                    String roleValue = roleClaim != null ? roleClaim.trim() : "";
                    // Ensure ROLE_ prefix for Spring security when using hasRole()
                    if (!roleValue.startsWith("ROLE_")) {
                        roleValue = "ROLE_" + roleValue;
                    }
                    var granted = List.of(new SimpleGrantedAuthority(roleValue));
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, granted);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
