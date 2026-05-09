package com.duoc.backendS8.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.backendS8.dto.VeterinarioRequest;
import com.duoc.backendS8.dto.VeterinarioResponse;
import com.duoc.backendS8.service.VeterinarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/veterinarios")
public class VeterinarioController {

	private final VeterinarioService veterinarioService;

	public VeterinarioController(VeterinarioService veterinarioService) {
		this.veterinarioService = veterinarioService;
	}

	@GetMapping
	public List<VeterinarioResponse> listar() {
		return veterinarioService.listarTodos();
	}

	@GetMapping("/activos")
	public List<VeterinarioResponse> activos() {
		return veterinarioService.listarActivos();
	}

	@GetMapping("/{id:\\d+}")
	public VeterinarioResponse obtener(@PathVariable Long id) {
		return veterinarioService.obtener(id);
	}

	@PostMapping
	public ResponseEntity<VeterinarioResponse> crear(@Valid @RequestBody VeterinarioRequest request) {
		return ResponseEntity.status(201).body(veterinarioService.crear(request));
	}

	@PutMapping("/{id:\\d+}")
	public VeterinarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody VeterinarioRequest request) {
		return veterinarioService.actualizar(id, request);
	}

	@DeleteMapping("/{id:\\d+}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		veterinarioService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
