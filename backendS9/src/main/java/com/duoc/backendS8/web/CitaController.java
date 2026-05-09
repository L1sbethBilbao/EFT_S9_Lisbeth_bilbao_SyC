package com.duoc.backendS8.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.backendS8.dto.CitaRequest;
import com.duoc.backendS8.dto.CitaResponse;
import com.duoc.backendS8.service.CitaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

	private final CitaService citaService;

	public CitaController(CitaService citaService) {
		this.citaService = citaService;
	}

	@GetMapping
	public List<CitaResponse> listar(Authentication authentication) {
		return citaService.listar(authentication);
	}

	@GetMapping("/{id}")
	public CitaResponse obtener(@PathVariable Long id, Authentication authentication) {
		return citaService.obtener(id, authentication);
	}

	@PostMapping
	public ResponseEntity<CitaResponse> crear(@Valid @RequestBody CitaRequest request, Authentication authentication) {
		return ResponseEntity.status(201).body(citaService.crear(request, authentication));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> cancelar(@PathVariable Long id, Authentication authentication) {
		citaService.cancelar(id, authentication);
		return ResponseEntity.noContent().build();
	}
}
