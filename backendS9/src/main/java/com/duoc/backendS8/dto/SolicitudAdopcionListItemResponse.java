package com.duoc.backendS8.dto;

import java.time.Instant;

import com.duoc.backendS8.entity.EstadoSolicitudAdopcion;

public record SolicitudAdopcionListItemResponse(
		Long id,
		String codigoSeguimiento,
		Long animalId,
		String animalNombre,
		String nombreSolicitante,
		String email,
		EstadoSolicitudAdopcion estado,
		Instant createdAt) {
}
