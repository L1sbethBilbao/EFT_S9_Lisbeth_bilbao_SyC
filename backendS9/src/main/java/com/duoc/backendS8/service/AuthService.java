package com.duoc.backendS8.service;

import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.duoc.backendS8.config.JwtProperties;
import com.duoc.backendS8.dto.LoginResponse;
import com.duoc.backendS8.entity.RolUsuario;
import com.duoc.backendS8.entity.Usuario;
import com.duoc.backendS8.repository.UsuarioRepository;

@Service
public class AuthService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final JwtProperties jwtProperties;

	public AuthService(
			UsuarioRepository usuarioRepository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService,
			JwtProperties jwtProperties) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.jwtProperties = jwtProperties;
	}

	public LoginResponse login(String username, String password) {
		Usuario usuario = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
		if (!passwordEncoder.matches(password, usuario.getPassword())) {
			throw new BadCredentialsException("Credenciales inválidas");
		}
		RolUsuario rol = usuario.getRol() != null ? usuario.getRol() : RolUsuario.GESTOR;
		String token = jwtService.generateToken(usuario.getUsername(), List.of(rol));
		long seconds = jwtProperties.expirationMs() / 1000;
		return new LoginResponse(token, "Bearer", seconds, List.of(rol.name()));
	}
}
