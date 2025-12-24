package com.example.enotes.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private String secretKey = "c2VjcmV0LWtleS1mb3ItZW5vdGVzLXByb2plY3Qtc2VjdXJpdHktYmFzZTY0";

	public String extractUsername(String token)
	{
		return extractClaims(token,Claims::getSubject);
	}

	private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	
	public String generateToken(UserDetails userDetails)
	{
		return generateToken(new HashMap<>(),userDetails);
	}

	private String generateToken(Map<String, Object> extractClaims, UserDetails userDetails) {
		return Jwts.builder().setClaims(extractClaims)
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
				.signWith(getSignInKey(),SignatureAlgorithm.HS256)
				.compact();
	}

	private Key getSignInKey() {
	    byte[] keyBytes = Decoders.BASE64.decode(secretKey); 
	    return Keys.hmacShaKeyFor(keyBytes);
	}
	
	public boolean isTokenValid(String token, UserDetails userDetails)
	{
		final String userName = extractUsername(token);
		return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	private boolean isTokenExpired(String token) {
			return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaims(token, Claims::getExpiration);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignInKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	
	
	
}
