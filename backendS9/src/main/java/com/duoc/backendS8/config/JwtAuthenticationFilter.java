package com.duoc.backendS8.config;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.duoc.backendS8.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (header != null && header.startsWith("Bearer ")) {
			String token = header.substring(7).trim();
			try {
				String username = jwtService.extractUsername(token);
				if (username != null && jwtService.isTokenValid(token, username)) {
					var existing = SecurityContextHolder.getContext().getAuthentication();
					if (existing == null || existing instanceof AnonymousAuthenticationToken) {
						List<SimpleGrantedAuthority> authorities = jwtService.extractRoleNames(token).stream()
								.map(r -> new SimpleGrantedAuthority("ROLE_" + r))
								.collect(Collectors.toList());
						if (authorities.isEmpty()) {
							authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
						}
						UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
								username,
								null,
								authorities);
						SecurityContextHolder.getContext().setAuthentication(authentication);
					}
				}
			}
			catch (RuntimeException ignored) {
				SecurityContextHolder.clearContext();
			}
		}
		filterChain.doFilter(request, response);
	}
}
