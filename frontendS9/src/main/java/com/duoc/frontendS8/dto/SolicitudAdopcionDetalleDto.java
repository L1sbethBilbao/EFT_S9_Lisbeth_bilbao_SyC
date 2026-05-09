package com.duoc.frontendS8.dto;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SolicitudAdopcionDetalleDto(
		Long id,
		String codigoSeguimiento,
		Long animalId,
		String animalNombre,
		String nombreCompleto,
		String email,
		String telefono,
		String direccion,
		String ciudad,
		String tipoVivienda,
		Integer personasEnHogar,
		Boolean tieneNinos,
		Boolean tieneOtrasMascotas,
		String experienciaMascotas,
		String motivacionAdopcion,
		String estado,
		Instant createdAt,
		List<MensajeAdopcionDto> mensajes) {
}
