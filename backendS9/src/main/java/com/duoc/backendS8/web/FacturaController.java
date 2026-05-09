package com.duoc.backendS8.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.backendS8.dto.EnviarFacturaRequest;
import com.duoc.backendS8.dto.FacturaRequest;
import com.duoc.backendS8.dto.FacturaResponse;
import com.duoc.backendS8.service.FacturaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

	private final FacturaService facturaService;

	public FacturaController(FacturaService facturaService) {
		this.facturaService = facturaService;
	}

	@GetMapping
	public List<FacturaResponse> listar() {
		return facturaService.listar();
	}

	@GetMapping("/{id}")
	public FacturaResponse obtener(@PathVariable Long id) {
		return facturaService.obtener(id);
	}

	@PostMapping
	public ResponseEntity<FacturaResponse> crear(@Valid @RequestBody FacturaRequest request) {
		return ResponseEntity.status(201).body(facturaService.crear(request));
	}

	@PostMapping("/{id}/enviar-correo")
	public ResponseEntity<Void> enviarCorreo(@PathVariable Long id, @Valid @RequestBody EnviarFacturaRequest request) {
		facturaService.enviarCorreo(id, request.email());
		return ResponseEntity.noContent().build();
	}
}
