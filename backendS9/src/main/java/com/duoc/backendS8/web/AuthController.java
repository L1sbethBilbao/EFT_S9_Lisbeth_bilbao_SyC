package com.duoc.backendS8.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.backendS8.dto.LoginRequest;
import com.duoc.backendS8.dto.LoginResponse;
import com.duoc.backendS8.dto.SessionUsuarioResponse;
import com.duoc.backendS8.entity.Usuario;
import com.duoc.backendS8.repository.UsuarioRepository;
import com.duoc.backendS8.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	private final UsuarioRepository usuarioRepository;

	public AuthController(AuthService authService, UsuarioRepository usuarioRepository) {
		this.authService = authService;
		this.usuarioRepository = usuarioRepository;
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request.username(), request.password()));
	}

	@GetMapping("/me")
	public ResponseEntity<SessionUsuarioResponse> sesion(Authentication authentication) {
		Usuario u = usuarioRepository.findByUsernameFetchingVeterinario(authentication.getName())
				.orElseThrow();
		Long vid = u.getVeterinario() != null ? u.getVeterinario().getId() : null;
		String vnom = u.getVeterinario() != null ? u.getVeterinario().getNombre() : null;
		String rol = u.getRol() != null ? u.getRol().name() : "";
		return ResponseEntity.ok(new SessionUsuarioResponse(u.getUsername(), rol, vid, vnom));
	}
}
