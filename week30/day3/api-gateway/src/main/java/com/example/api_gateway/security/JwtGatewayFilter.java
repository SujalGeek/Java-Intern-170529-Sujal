//package com.example.api_gateway.security;
//
//import java.util.List; 
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//
//import io.jsonwebtoken.Claims;
//import reactor.core.publisher.Mono;
//
//@Component
//public class JwtGatewayFilter implements GlobalFilter {
//
//    @Autowired
//    private JwtService jwtService;
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//
//        String path = exchange.getRequest().getURI().getPath();
//
//        // Allow login without token
//        if (path.startsWith("/api/auth")) {
//            return chain.filter(exchange);
//        }
//
//        List<String> authHeaders =
//                exchange.getRequest().getHeaders().get("Authorization");
//
//        if (authHeaders == null || authHeaders.isEmpty()) {
//            return unauthorized(exchange);
//        }
//
//        String authHeader = authHeaders.get(0);
//
//        if (!authHeader.startsWith("Bearer ")) {
//            return unauthorized(exchange);
//        }
//
//        String token = authHeader.substring(7);
//
//        try {
//            if (!jwtService.validateToken(token)) {
//                return unauthorized(exchange);
//            }
//
//            Claims claims = jwtService.extractClaim(token, c -> c);
//            Object roleObj = claims.get("role");
//            Integer role = (roleObj instanceof Integer) ? (Integer) roleObj : Integer.parseInt(roleObj.toString());
//            System.out.println("Path: "+path);
//            System.out.println("Role: "+role);
//
//            // 🔥 ROLE BASED RESTRICTIONS
////            if (path.startsWith("/api/course") && role != 2) {
////                return unauthorized(exchange);
////            }
////
////            if (path.startsWith("/api/users") && role != 1) {
////                return unauthorized(exchange);
////            }
//            
//            if (!hasAccess(path, role)) {
//                return unauthorized(exchange);
//            }
//
//            ServerWebExchange mutatedExchange = exchange.mutate()
//            	    .request(builder -> builder
//            	        .header("X-User-Id", claims.get("userId").toString())
//            	        .header("X-User-Role", role.toString())
//            	        .header("X-Username", claims.getSubject())
//            	    )
//            	    .build();
//
//            	return chain.filter(mutatedExchange);
//            
//        } catch (Exception e) {
//            return unauthorized(exchange);
//        }
//
//        
////        return chain.filter(exchange);
//    }
//
//    private Mono<Void> unauthorized(ServerWebExchange exchange) {
//        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//        return exchange.getResponse().setComplete();
//    }
//    
// 
//    private boolean hasAccess(String path, Integer role) {
//
//        // ADMIN → full access
//        if (role == 1) {
//            return true;
//        }
//
//        // TEACHER
//        if (role == 2) {
//
//            if (path.startsWith("/api/course")) return true;
//            if (path.startsWith("/api/quiz/generate")) return true;
//            if (path.startsWith("/api/assignment/generate")) return true;
//            if (path.startsWith("/api/assignment/evaluate")) return true;
//            if (path.startsWith("/api/prediction")) return true;
//            if (path.startsWith("/api/analytics")) return true;
//            if (path.startsWith("/api/exam-result")) return true;
//            if (path.startsWith("/nlp")) return true;
//
//            return false;
//        }
//
//        // STUDENT
//        if (role == 3) {
//
//            if (path.startsWith("/api/course/enrolled")) return true;
//            if (path.startsWith("/api/quiz/submit")) return true;
//            if (path.startsWith("/api/assignment/submit")) return true;
//            if (path.startsWith("/api/performance")) return true;
//            if (path.startsWith("/api/prediction")) return true;
//            if (path.startsWith("/api/exam-result")) return true;
//
//            return false;
//        }
//
//        return false;
//    }
//}

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

        // 1. Whitlist auth paths
        if (path.startsWith("/api/auth")) {
            return chain.filter(exchange);
        }

        // 2. Check Authorization Header
        List<String> authHeaders = exchange.getRequest().getHeaders().get("Authorization");
        if (authHeaders == null || authHeaders.isEmpty()) {
            return unauthorized(exchange);
        }

        String authHeader = authHeaders.get(0);
        if (!authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(7);

        try {
            // 3. Validate Signature and Expiry
            if (!jwtService.validateToken(token)) {
                System.out.println("Invalid Token for path: " + path);
                return unauthorized(exchange);
            }

            Claims claims = jwtService.extractClaim(token, c -> c);
            
            // 4. Robust Role Parsing (Handles Integer or String from JWT)
            Object roleObj = claims.get("role");
            if (roleObj == null) {
                System.out.println("Role missing in token");
                return unauthorized(exchange);
            }
            Integer role = (roleObj instanceof Integer) ? (Integer) roleObj : Integer.parseInt(roleObj.toString());

            // 5. Check Access
            if (!hasAccess(path, role)) {
                System.out.println("Access Denied for Role: " + role + " on Path: " + path);
                return unauthorized(exchange);
            }

            // 6. Mutate Request with Headers for Microservices
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(builder -> builder
                        .header("X-User-Id", claims.get("userId").toString())
                        .header("X-User-Role", role.toString())
                        .header("X-Username", claims.getSubject())
                    )
                    .build();

            return chain.filter(mutatedExchange);

        } catch (Exception e) {
            // LOG THE REAL ERROR BRO
            System.err.println("JWT FILTER ERROR: " + e.getMessage());
            return unauthorized(exchange);
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

//    private boolean hasAccess(String path, Integer role) {
//        if (role == 1) return true; // ADMIN
//        
//        if (role == 2) { // TEACHER
//            return path.startsWith("/api/course") || path.startsWith("/api/quiz/generate") || 
//                   path.startsWith("/api/assignment/generate") || path.startsWith("/api/assignment/evaluate") || 
//                   path.startsWith("/api/prediction") || path.startsWith("/api/analytics") || 
//                   path.startsWith("/api/exam-result") || path.startsWith("/nlp");
//        }
//
//        if (role == 3) { // STUDENT
//            return path.startsWith("/api/course/enrolled") || path.startsWith("/api/quiz/submit") || 
//                   path.startsWith("/api/assignment/submit") || path.startsWith("/api/performance") || 
//                   path.startsWith("/api/prediction") || path.startsWith("/api/exam-result");
//        }
//        return false;
//    }
    	
    	private boolean hasAccess(String path, Integer role) {

    	    if (role == 1) return true; // ADMIN

    	    if (role == 2) { // TEACHER
    	        String[] teacherPaths = {
    	            "/api/course",
    	            "/api/enrollments",
    	            "/api/submissions",
    	            "/api/quiz",
    	            "/api/assignment",
    	            "/api/exam-results",
    	            "/api/performance",
    	            "/api/predict",
	    	         "/api/analytics",
    	            "/api/v1/exams",
    	            "/nlp"
    	        };

    	        for (String p : teacherPaths) {
    	            if (path.startsWith(p)) return true;
    	        }
    	        return false;
    	    }

    	    if (role == 3) { // STUDENT
    	        return path.startsWith("/api/course/enrolled") ||
//    	               path.startsWith("/api/quiz/submit") ||
    	               path.startsWith("/api/enrollments") ||
    	               path.startsWith("/api/quiz") ||
    	               path.startsWith("/api/v1/exams") ||
    	               path.startsWith("/api/assignments/submit") ||
    	               path.startsWith("/api/performance") ||
    	               path.startsWith("/api/predict") ||
    	               path.startsWith("/api/exam-results");
    	    }
    	    
//    	     if (path.startsWith("/api/course/enrolled")) return true;
//           if (path.startsWith("/api/quiz/submit")) return true;
//           if (path.startsWith("/api/assignment/submit")) return true;
//           if (path.startsWith("/api/performance")) return true;
//           if (path.startsWith("/api/prediction")) return true;
//           if (path.startsWith("/api/exam-result")) return true;


    	    return false;
    	}
}