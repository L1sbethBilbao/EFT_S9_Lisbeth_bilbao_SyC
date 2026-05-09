package com.duoc.backendS8.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.duoc.backendS8.dto.LoginResponse;
import com.duoc.backendS8.repository.UsuarioRepository;
import com.duoc.backendS8.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerStandaloneTest {

	@Mock
	private AuthService authService;
	@Mock
	private UsuarioRepository usuarioRepository;

	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService, usuarioRepository)).build();
	}

	@Test
	void loginDevuelveToken() throws Exception {
		when(authService.login("maria", "Duoc2026!"))
				.thenReturn(new LoginResponse("tok-1", "Bearer", 3600, List.of("COORDINADOR")));
		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"maria\",\"password\":\"Duoc2026!\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value("tok-1"))
				.andExpect(jsonPath("$.roles[0]").value("COORDINADOR"));
	}
}
