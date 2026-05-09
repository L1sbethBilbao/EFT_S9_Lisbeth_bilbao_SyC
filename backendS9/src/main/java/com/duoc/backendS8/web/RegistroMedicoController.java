package com.duoc.backendS8.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.backendS8.dto.RegistroMedicoRequest;
import com.duoc.backendS8.dto.RegistroMedicoResponse;
import com.duoc.backendS8.service.RegistroMedicoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/registros-medicos")
public class RegistroMedicoController {

	private final RegistroMedicoService registroMedicoService;

	public RegistroMedicoController(RegistroMedicoService registroMedicoService) {
		this.registroMedicoService = registroMedicoService;
	}

	@GetMapping
	public List<RegistroMedicoResponse> listar() {
		return registroMedicoService.listar();
	}

	@GetMapping("/{id}")
	public RegistroMedicoResponse obtener(@PathVariable Long id) {
		return registroMedicoService.obtener(id);
	}

	@PostMapping
	public ResponseEntity<RegistroMedicoResponse> crear(@Valid @RequestBody RegistroMedicoRequest request) {
		return ResponseEntity.status(201).body(registroMedicoService.crear(request));
	}
}
