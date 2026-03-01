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

        // Allow login without token
        if (path.startsWith("/api/auth")) {
            return chain.filter(exchange);
        }

        List<String> authHeaders =
                exchange.getRequest().getHeaders().get("Authorization");

        if (authHeaders == null || authHeaders.isEmpty()) {
            return unauthorized(exchange);
        }

        String authHeader = authHeaders.get(0);

        if (!authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(7);

        try {
            if (!jwtService.validateToken(token)) {
                return unauthorized(exchange);
            }

            Claims claims = jwtService.extractClaim(token, c -> c);
            Integer role = claims.get("role", Integer.class);
            System.out.println("Path: "+path);
            System.out.println("Role: "+role);

            // 🔥 ROLE BASED RESTRICTIONS
//            if (path.startsWith("/api/course") && role != 2) {
//                return unauthorized(exchange);
//            }
//
//            if (path.startsWith("/api/users") && role != 1) {
//                return unauthorized(exchange);
//            }
            
            if (!hasAccess(path, role)) {
                return unauthorized(exchange);
            }

            ServerWebExchange mutatedExchange = exchange.mutate()
            	    .request(builder -> builder
            	        .header("X-User-Id", claims.get("userId").toString())
            	        .header("X-User-Role", role.toString())
            	        .header("X-Username", claims.getSubject())
            	    )
            	    .build();

            	return chain.filter(mutatedExchange);
            
        } catch (Exception e) {
            return unauthorized(exchange);
        }

        
//        return chain.filter(exchange);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
    
 
    private boolean hasAccess(String path, Integer role) {

        // ADMIN → full access
        if (role == 1) {
            return true;
        }

        // TEACHER
        if (role == 2) {

            if (path.startsWith("/api/course")) return true;
            if (path.startsWith("/api/quiz/generate")) return true;
            if (path.startsWith("/api/assignment/generate")) return true;
            if (path.startsWith("/api/assignment/evaluate")) return true;
            if (path.startsWith("/api/prediction")) return true;
            if (path.startsWith("/api/analytics")) return true;
            if (path.startsWith("/api/exam-result")) return true;
            if (path.startsWith("/nlp")) return true;

            return false;
        }

        // STUDENT
        if (role == 3) {

            if (path.startsWith("/api/course/enrolled")) return true;
            if (path.startsWith("/api/quiz/submit")) return true;
            if (path.startsWith("/api/assignment/submit")) return true;
            if (path.startsWith("/api/performance")) return true;
            if (path.startsWith("/api/prediction")) return true;
            if (path.startsWith("/api/exam-result")) return true;

            return false;
        }

        return false;
    }
}