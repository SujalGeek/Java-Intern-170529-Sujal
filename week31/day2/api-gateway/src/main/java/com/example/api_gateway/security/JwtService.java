//package com.example.api_gateway.security;
//
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.security.Key;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//@Service
//public class JwtService {
//
////    @Value("${jwt.secret}")
////    private String secret;
////
////    @Value("${jwt.expiration}")
////    private Long expiration;
////
////    public String generateToken(String username, Long userId, Integer role) {
////        Map<String, Object> claims = new HashMap<>();
////        claims.put("userId", userId);
////        claims.put("role", role);
////        return createToken(claims, username);
////    }
////
////    private String createToken(Map<String, Object> claims, String subject) {
////        return Jwts.builder()
////                .setClaims(claims)
////                .setSubject(subject)
////                .setIssuedAt(new Date(System.currentTimeMillis()))
////                .setExpiration(new Date(System.currentTimeMillis() + expiration))
////                .signWith(getSignKey(), SignatureAlgorithm.HS256)
////                .compact();
////    }
//	@Value("${jwt.secret}")
//    private String secret;
//
//    @Value("${jwt.expiration}")
//    private Long jwtExpiration;
//
//    @Value("${jwt.refresh-expiration}")
//    private Long refreshExpiration;
//
//    // Generate Access Token
//    public String generateToken(String username, Long userId, Integer role) {
//        return buildToken(new HashMap<>(Map.of("userId", userId, "role", role)), username, jwtExpiration);
//    }
//
//    // Generate Refresh Token (Usually fewer claims for security)
//    public String generateRefreshToken(String username) {
//        return buildToken(new HashMap<>(), username, refreshExpiration);
//    }
//
//    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
//        return Jwts.builder()
//                .setClaims(extraClaims)
//                .setSubject(subject)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + expiration))
//                .signWith(getSignKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    // UPDATED: Better validation to catch errors
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
//            return true;
//        } catch (Exception e) {
//            System.out.println("JWT Validation Error: " + e.getMessage());
//            return false;
//        }
//    }
//    private Key getSignKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(secret);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSignKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
////
////    public Boolean validateToken(String token) {
////        return !isTokenExpired(token);
////    }
//
//    private Boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    public Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//}
package com.example.api_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    // --- YOUR ORIGINAL TOKEN GENERATION LOGIC ---

    public String generateToken(String username, Long userId, Integer role) {
        return buildToken(new HashMap<>(Map.of("userId", userId, "role", role)), username, jwtExpiration);
    }

    public String generateRefreshToken(String username) {
        return buildToken(new HashMap<>(), username, refreshExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // --- THE FIX: ROBUST VALIDATION TO STOP 401 ---

    public boolean validateToken(String token) {
        try {
            // This line checks signature AND expiration in one go
            Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // This will print to your console exactly why it failed (Expired, Wrong Key, etc.)
            System.err.println("JWT Validation Error: " + e.getMessage());
            return false;
        }
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // --- YOUR ORIGINAL EXTRACTION METHODS (RESTORED) ---

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // Updated to use parserBuilder for consistency with modern JJWT
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}