package com.duoc.backendS8.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.duoc.backendS8.config.JwtProperties;
import com.duoc.backendS8.entity.RolUsuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

class JwtServiceTest {

	private JwtService jwtService;

	@BeforeEach
	void setUp() {
		JwtProperties props = new JwtProperties("test-secret-key-for-jwt-must-be-at-least-32-chars-long!!", 3_600_000L);
		jwtService = new JwtService(props);
	}

	@Test
	void generateAndReadUsernameAndRoles() {
		String token = jwtService.generateToken("maria", List.of(RolUsuario.COORDINADOR));
		assertThat(jwtService.extractUsername(token)).isEqualTo("maria");
		assertThat(jwtService.extractRoleNames(token)).containsExactly("COORDINADOR");
		assertThat(jwtService.isTokenValid(token, "maria")).isTrue();
		assertThat(jwtService.isTokenValid(token)).isTrue();
	}

	@Test
	void isTokenValidFalseForWrongUser() {
		String token = jwtService.generateToken("maria", List.of(RolUsuario.GESTOR));
		assertThat(jwtService.isTokenValid(token, "otro")).isFalse();
	}

	@Test
	void extractRoleNamesEmptyWhenClaimMissing() {
		JwtProperties shortLived = new JwtProperties("test-secret-key-for-jwt-must-be-at-least-32-chars-long!!", 60_000L);
		JwtService svc = new JwtService(shortLived);
		String token = Jwts.builder()
				.setSubject("solo")
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 60_000L))
				.signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
						shortLived.secret().getBytes(java.nio.charset.StandardCharsets.UTF_8)))
				.compact();
		assertThat(svc.extractRoleNames(token)).isEmpty();
	}

	@Test
	void invalidTokenReturnsFalseForOverloadWithoutUsername() {
		assertThat(jwtService.isTokenValid("no-es-un-jwt")).isFalse();
	}

	@Test
	void parseClaimsExposesRolesClaim() {
		String token = jwtService.generateToken("luis", List.of(RolUsuario.VETERINARIO, RolUsuario.GESTOR));
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
						"test-secret-key-for-jwt-must-be-at-least-32-chars-long!!".getBytes(java.nio.charset.StandardCharsets.UTF_8)))
				.build()
				.parseClaimsJws(token)
				.getBody();
		assertThat(claims.get(JwtService.CLAIM_ROLES, List.class)).hasSize(2);
	}
}
