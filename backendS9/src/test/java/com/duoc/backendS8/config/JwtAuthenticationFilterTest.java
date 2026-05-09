package com.duoc.backendS8.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.duoc.backendS8.entity.RolUsuario;
import com.duoc.backendS8.service.JwtService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

	@Mock
	private JwtService jwtService;

	@AfterEach
	void clear() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void sinHeaderNoAutentica() throws ServletException, IOException {
		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse res = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();
		filter.doFilterInternal(req, res, chain);
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
	}

	@Test
	void bearerValidoEstableceAutenticacion() throws ServletException, IOException {
		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);
		String token = "abc";
		when(jwtService.extractUsername(token)).thenReturn("pedro");
		when(jwtService.isTokenValid(token, "pedro")).thenReturn(true);
		when(jwtService.extractRoleNames(token)).thenReturn(List.of(RolUsuario.VETERINARIO.name()));

		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addHeader("Authorization", "Bearer " + token);
		MockHttpServletResponse res = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();
		filter.doFilterInternal(req, res, chain);

		assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("pedro");
		assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
				.extracting(Object::toString)
				.contains("ROLE_VETERINARIO");
	}

	@Test
	void noSobrescribeSiYaHayAutenticacionNoAnonima() throws ServletException, IOException {
		SecurityContextHolder.getContext().setAuthentication(
				new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
						"ya", null, List.of(new SimpleGrantedAuthority("ROLE_COORDINADOR"))));

		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);
		String token = "abc";
		when(jwtService.extractUsername(token)).thenReturn("otro");
		when(jwtService.isTokenValid(token, "otro")).thenReturn(true);

		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addHeader("Authorization", "Bearer " + token);
		filter.doFilterInternal(req, new MockHttpServletResponse(), new MockFilterChain());

		assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("ya");
	}

	@Test
	void tokenInvalidoLimpiaContextoAnonimo() throws ServletException, IOException {
		SecurityContextHolder.getContext().setAuthentication(
				new AnonymousAuthenticationToken("k", "anon", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);
		when(jwtService.extractUsername("bad")).thenThrow(new RuntimeException("jwt"));

		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addHeader("Authorization", "Bearer bad");
		filter.doFilterInternal(req, new MockHttpServletResponse(), new MockFilterChain());

		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
	}
}
