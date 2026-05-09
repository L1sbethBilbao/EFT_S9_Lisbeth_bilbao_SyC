package com.duoc.backendS8.service;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.duoc.backendS8.config.JwtProperties;
import com.duoc.backendS8.entity.RolUsuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	public static final String CLAIM_ROLES = "roles";

	private final JwtProperties jwtProperties;

	public JwtService(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
	}

	public String generateToken(String username, Collection<RolUsuario> roles) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + jwtProperties.expirationMs());
		List<String> roleNames = roles.stream().map(Enum::name).toList();
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(now)
				.setExpiration(expiry)
				.claim(CLAIM_ROLES, roleNames)
				.signWith(signingKey())
				.compact();
	}

	public String extractUsername(String token) {
		return parseClaims(token).getSubject();
	}

	@SuppressWarnings("unchecked")
	public List<String> extractRoleNames(String token) {
		Object raw = parseClaims(token).get(CLAIM_ROLES);
		if (raw instanceof List<?> list) {
			return list.stream().map(Object::toString).toList();
		}
		return List.of();
	}

	public boolean isTokenValid(String token, String expectedUsername) {
		String subject = extractUsername(token);
		return subject != null && subject.equals(expectedUsername) && !isExpired(token);
	}

	public boolean isTokenValid(String token) {
		try {
			parseClaims(token);
			return !isExpired(token);
		}
		catch (RuntimeException e) {
			return false;
		}
	}

	private boolean isExpired(String token) {
		return parseClaims(token).getExpiration().before(new Date());
	}

	private Claims parseClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(signingKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	private SecretKey signingKey() {
		byte[] bytes = jwtProperties.secret().getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(bytes);
	}
}
