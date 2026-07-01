package com.ecomerce.src.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value("${application.security.jwt.secret-key}")
	private String secretKey;

	@Value("${application.security.jwt.expiration-ms}")
	private long expirationMs;

	public String generateToken(UserDetails userDetails) {
		return generateToken(userDetails, null);
	}

	public String generateToken(UserDetails userDetails, Integer userId) {
		long now = System.currentTimeMillis();
		// El subject del token es el id del usuario (no el email/nombre).
		String subject = userId != null ? String.valueOf(userId) : userDetails.getUsername();
		return Jwts.builder()
				.subject(subject)
				.issuedAt(new Date(now))
				.expiration(new Date(now + expirationMs))
				.signWith(getSigningKey())
				.compact();
	}

	public String extractSubject(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public boolean isTokenValid(String token) {
		return !isTokenExpired(token);
	}

	public Integer extractUserId(String token) {
		String subject = extractSubject(token);
		if (subject == null) {
			return null;
		}
		try {
			return Integer.valueOf(subject);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private boolean isTokenExpired(String token) {
		Date expiration = extractClaim(token, Claims::getExpiration);
		return expiration.before(new Date());
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		Claims claims = Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
		return claimsResolver.apply(claims);
	}

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	}
}
