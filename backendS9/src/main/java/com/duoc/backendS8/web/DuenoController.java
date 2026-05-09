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

import com.duoc.backendS8.dto.DuenoRequest;
import com.duoc.backendS8.dto.DuenoResponse;
import com.duoc.backendS8.service.DuenoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/duenos")
public class DuenoController {

	private final DuenoService duenoService;

	public DuenoController(DuenoService duenoService) {
		this.duenoService = duenoService;
	}

	@GetMapping
	public List<DuenoResponse> listar() {
		return duenoService.listar();
	}

	@GetMapping("/{id}")
	public DuenoResponse obtener(@PathVariable Long id) {
		return duenoService.obtener(id);
	}

	@PostMapping
	public ResponseEntity<DuenoResponse> crear(@Valid @RequestBody DuenoRequest request) {
		return ResponseEntity.status(201).body(duenoService.crear(request));
	}

	@PutMapping("/{id}")
	public DuenoResponse actualizar(@PathVariable Long id, @Valid @RequestBody DuenoRequest request) {
		return duenoService.actualizar(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		duenoService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
