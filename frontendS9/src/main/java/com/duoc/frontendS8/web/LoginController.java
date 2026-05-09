package com.duoc.frontendS8.web;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.duoc.frontendS8.dto.LoginApiResponse;
import com.duoc.frontendS8.exception.BackendUnavailableException;
import com.duoc.frontendS8.service.BackendApiClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

@Controller
public class LoginController {

	private final BackendApiClient backendApiClient;
	private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

	public LoginController(BackendApiClient backendApiClient) {
		this.backendApiClient = backendApiClient;
	}

	@GetMapping("/login")
	public String loginPage(
			@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "backend", required = false) String backend,
			Model model) {
		if (error != null) {
			model.addAttribute("loginError", "Usuario o contraseña incorrectos.");
		}
		if (backend != null) {
			model.addAttribute(
					"loginError",
					"No se pudo conectar con el servidor (API en puerto 8080). Comprueba que el backend y MySQL estén en ejecución.");
		}
		return "login";
	}

	@PostMapping("/login")
	public String login(
			@RequestParam String username,
			@RequestParam String password,
			HttpServletRequest request,
			HttpServletResponse response) {
		final LoginApiResponse api;
		try {
			api = backendApiClient.login(username, password);
		}
		catch (BackendUnavailableException e) {
			return "redirect:/login?backend";
		}
		if (api == null || api.token() == null || api.token().isBlank()) {
			return "redirect:/login?error";
		}
		HttpSession session = request.getSession(true);
		session.setAttribute(BackendApiClient.SESSION_JWT, api.token());

		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		if (api.roles() != null) {
			for (String r : api.roles()) {
				if (r != null && !r.isBlank()) {
					authorities.add(new SimpleGrantedAuthority(
							r.startsWith("ROLE_") ? r : "ROLE_" + r));
				}
			}
		}
		if (authorities.isEmpty()) {
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		}

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				username,
				null,
				authorities);
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
		securityContextRepository.saveContext(context, request, response);
		return "redirect:/";
	}
}
