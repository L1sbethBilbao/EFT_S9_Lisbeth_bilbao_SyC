package com.duoc.backendS8.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.duoc.backendS8.entity.RolUsuario;
import com.duoc.backendS8.entity.Usuario;
import com.duoc.backendS8.repository.UsuarioRepository;
import com.duoc.backendS8.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerSesionTest {

	@Mock
	private AuthService authService;
	@Mock
	private UsuarioRepository usuarioRepository;

	@Test
	void sesionDevuelveUsuarioActual() {
		var controller = new AuthController(authService, usuarioRepository);
		var auth = new UsernamePasswordAuthenticationToken("maria", null, List.of());
		Usuario u = Usuario.builder().username("maria").rol(RolUsuario.COORDINADOR).build();
		when(usuarioRepository.findByUsernameFetchingVeterinario("maria")).thenReturn(java.util.Optional.of(u));

		var body = controller.sesion(auth).getBody();

		assertThat(body).isNotNull();
		assertThat(body.username()).isEqualTo("maria");
		assertThat(body.rol()).isEqualTo("COORDINADOR");
	}
}
