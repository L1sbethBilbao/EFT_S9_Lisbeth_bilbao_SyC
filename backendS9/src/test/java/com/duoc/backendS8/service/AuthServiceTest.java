package com.duoc.backendS8.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.duoc.backendS8.config.JwtProperties;
import com.duoc.backendS8.dto.LoginResponse;
import com.duoc.backendS8.entity.RolUsuario;
import com.duoc.backendS8.entity.Usuario;
import com.duoc.backendS8.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UsuarioRepository usuarioRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private JwtService jwtService;
	@Mock
	private JwtProperties jwtProperties;

	@InjectMocks
	private AuthService authService;

	private Usuario usuarioMaria;

	@BeforeEach
	void setUp() {
		usuarioMaria = Usuario.builder()
				.id(1L)
				.username("maria")
				.password("hash")
				.rol(RolUsuario.COORDINADOR)
				.build();
	}

	@Test
	void loginSuccess() {
		when(jwtProperties.expirationMs()).thenReturn(3600_000L);
		when(usuarioRepository.findByUsername("maria")).thenReturn(Optional.of(usuarioMaria));
		when(passwordEncoder.matches("Duoc2026!", "hash")).thenReturn(true);
		when(jwtService.generateToken(eq("maria"), any())).thenReturn("jwt-token");

		LoginResponse r = authService.login("maria", "Duoc2026!");

		assertThat(r.token()).isEqualTo("jwt-token");
		assertThat(r.tokenType()).isEqualTo("Bearer");
		assertThat(r.expiresInSeconds()).isEqualTo(3600L);
		assertThat(r.roles()).containsExactly("COORDINADOR");
		verify(jwtService).generateToken(eq("maria"), any());
	}

	@Test
	void loginUnknownUser() {
		when(usuarioRepository.findByUsername("x")).thenReturn(Optional.empty());
		assertThatThrownBy(() -> authService.login("x", "p")).isInstanceOf(BadCredentialsException.class);
	}

	@Test
	void loginBadPassword() {
		when(usuarioRepository.findByUsername("maria")).thenReturn(Optional.of(usuarioMaria));
		when(passwordEncoder.matches("mala", "hash")).thenReturn(false);
		assertThatThrownBy(() -> authService.login("maria", "mala")).isInstanceOf(BadCredentialsException.class);
	}

	@Test
	void loginDefaultsToGestorWhenRolNull() {
		when(jwtProperties.expirationMs()).thenReturn(3600_000L);
		Usuario sinRol = Usuario.builder().username("u").password("h").rol(null).build();
		when(usuarioRepository.findByUsername("u")).thenReturn(Optional.of(sinRol));
		when(passwordEncoder.matches("p", "h")).thenReturn(true);
		when(jwtService.generateToken(eq("u"), any())).thenReturn("t");

		LoginResponse r = authService.login("u", "p");
		assertThat(r.roles()).containsExactly("GESTOR");
	}
}
