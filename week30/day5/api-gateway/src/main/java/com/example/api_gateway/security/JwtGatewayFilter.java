package com.example.api_gateway.security;

import java.util.List; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class JwtGatewayFilter implements GlobalFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        System.out.println("Gateway routing request: " + path);

        // 1. GLOBAL WHITELIST: Public endpoints
        if (path.contains("/api/auth/") || path.endsWith("/login") || path.endsWith("/register")) {
            return chain.filter(exchange);
        }

        // 2. AUTHORIZATION HEADER VALIDATION
        List<String> authHeaders = exchange.getRequest().getHeaders().get("Authorization");
        if (authHeaders == null || authHeaders.isEmpty() || !authHeaders.get(0).startsWith("Bearer ")) {
            System.out.println("Block: Missing/Invalid Authorization Header for " + path);
            return unauthorized(exchange);
        }

        String token = authHeaders.get(0).substring(7);

        try {
            // 3. TOKEN INTEGRITY SYNC
            if (!jwtService.validateToken(token)) {
                return unauthorized(exchange);
            }

            Claims claims = jwtService.extractClaim(token, c -> c);
            
            // 4. ROBUST ROLE EXTRACTION
            Object roleObj = claims.get("role");
            if (roleObj == null) return unauthorized(exchange);
            Integer role = (roleObj instanceof Integer) ? (Integer) roleObj : Integer.parseInt(roleObj.toString());

            // 5. SECURE PERMISSION HANDSHAKE
            if (!hasAccess(path, role)) {
                System.out.println("Access Denied: Role " + role + " attempted path " + path);
                return unauthorized(exchange);
            }

            // 6. REQUEST MUTATION: Attach Identity Headers for Microservices
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(builder -> builder
                        .header("X-User-Id", claims.get("userId").toString())
                        .header("X-User-Role", role.toString())
                        .header("X-Username", claims.getSubject())
                    )
                    .build();

            return chain.filter(mutatedExchange);

        } catch (Exception e) {
            System.err.println("CRITICAL: Gateway Filter Failure - " + e.getMessage());
            return unauthorized(exchange);
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    /**
     * Senior Authority Matrix
     */
    private boolean hasAccess(String path, Integer role) {
        // ADMIN: FULL ACCESS
        if (role == 1) return true;

        // TEACHER (Role 2): Curriculum & Grading Control
        if (role == 2) {
            String[] teacherPrefixes = {
                "/api/users/",        // To see their own profile
                "/api/course",        // CRUD courses
                "/api/enrollments",   // View class lists
                "/api/quiz",          // Generate quizzes
                "/api/assignment",    // Generate/Evaluate assignments
                "/api/exam-results",  // Grade hub
                "/api/performance",   // Analytics service
                "/api/predict",       // Prediction service
                "/api/analytics",     // Teacher metrics
                "/api/v1/exams",      // Midterm generation
                "/nlp"                // Python model sync
            };
            for (String prefix : teacherPrefixes) {
                if (path.startsWith(prefix)) return true;
            }
            return false;
        }

        // STUDENT (Role 3): Learning & Submission Control
        if (role == 3) {
        	if (path.equals("/api/course") || path.startsWith("/api/course/")) return true;
        	
            String[] studentPrefixes = {
                "/api/users/",               // To see their own profile
                "/api/course/enrolled",      // View their courses
                "/api/enrollments/my",
                "/api/enrollments",// Profile dashboard
                "/api/enrollments/student/", // Dashboard sync
                "/api/quiz/submit",    
                "/api/quiz",// Attempt quizzes
                "/api/v1/exams",   
                "/api/assignments",// Attempt midterms
                "/api/assignments/submit",   // Subjective submission
                "/api/performance",          // AI Insights
                "/api/analytics/student/",   // Individual metrics
                "/api/exam-results/my"       // Result view
            };
            for (String prefix : studentPrefixes) {
                if (path.startsWith(prefix)) return true;
            }
            return false;
        }

        return false;
    }
}