package com.duoc.backendS8.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.backendS8.dto.EstadoSolicitudPatchRequest;
import com.duoc.backendS8.dto.MensajeCoordinadorRequest;
import com.duoc.backendS8.dto.MensajeSeguimientoRequest;
import com.duoc.backendS8.dto.SolicitudAdopcionCreateRequest;
import com.duoc.backendS8.dto.SolicitudAdopcionCreatedResponse;
import com.duoc.backendS8.dto.SolicitudAdopcionDatosPatchRequest;
import com.duoc.backendS8.dto.SolicitudAdopcionDetalleResponse;
import com.duoc.backendS8.dto.SolicitudAdopcionListItemResponse;
import com.duoc.backendS8.dto.SolicitudAdopcionSeguimientoPublicoResponse;
import com.duoc.backendS8.service.SolicitudAdopcionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/adopciones")
public class AdopcionSolicitudController {

	private final SolicitudAdopcionService solicitudAdopcionService;

	public AdopcionSolicitudController(SolicitudAdopcionService solicitudAdopcionService) {
		this.solicitudAdopcionService = solicitudAdopcionService;
	}

	@PostMapping("/solicitudes")
	public ResponseEntity<SolicitudAdopcionCreatedResponse> crear(@Valid @RequestBody SolicitudAdopcionCreateRequest body) {
		return ResponseEntity.status(201).body(solicitudAdopcionService.crear(body));
	}

	@PostMapping("/seguimiento/mensajes")
	public ResponseEntity<Void> mensajeSolicitante(@Valid @RequestBody MensajeSeguimientoRequest body) {
		solicitudAdopcionService.agregarMensajeSolicitante(body);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/seguimiento/consulta")
	public SolicitudAdopcionSeguimientoPublicoResponse consultaPublica(@RequestParam String codigo) {
		return solicitudAdopcionService.obtenerVistaPublicaPorCodigo(codigo);
	}

	@GetMapping("/solicitudes")
	public List<SolicitudAdopcionListItemResponse> listarCoordinador() {
		return solicitudAdopcionService.listarParaCoordinador();
	}

	@GetMapping("/solicitudes/{id}")
	public SolicitudAdopcionDetalleResponse detalleCoordinador(@PathVariable Long id) {
		return solicitudAdopcionService.obtenerParaCoordinador(id);
	}

	@PostMapping("/solicitudes/{id}/mensajes")
	public ResponseEntity<Void> mensajeCoordinador(
			@PathVariable Long id,
			@Valid @RequestBody MensajeCoordinadorRequest body) {
		solicitudAdopcionService.agregarMensajeCoordinador(id, body);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/solicitudes/{id}/estado")
	public ResponseEntity<Void> estado(
			@PathVariable Long id,
			@Valid @RequestBody EstadoSolicitudPatchRequest body) {
		solicitudAdopcionService.actualizarEstado(id, body);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/solicitudes/{id}/datos")
	public ResponseEntity<Void> actualizarDatos(
			@PathVariable Long id,
			@Valid @RequestBody SolicitudAdopcionDatosPatchRequest body) {
		solicitudAdopcionService.actualizarDatosCoordinador(id, body);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/solicitudes/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		solicitudAdopcionService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
