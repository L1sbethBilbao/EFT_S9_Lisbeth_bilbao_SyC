package com.duoc.backendS8.dto;

public record SolicitudAdopcionCreatedResponse(
		Long id,
		String codigoSeguimiento,
		String animalNombre,
		String mensaje) {
}
