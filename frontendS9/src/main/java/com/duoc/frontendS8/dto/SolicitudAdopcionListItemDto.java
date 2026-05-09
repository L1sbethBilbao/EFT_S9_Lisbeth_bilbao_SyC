package com.duoc.frontendS8.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SolicitudAdopcionListItemDto(
		Long id,
		String codigoSeguimiento,
		Long animalId,
		String animalNombre,
		String nombreSolicitante,
		String email,
		String estado,
		Instant createdAt) {
}
