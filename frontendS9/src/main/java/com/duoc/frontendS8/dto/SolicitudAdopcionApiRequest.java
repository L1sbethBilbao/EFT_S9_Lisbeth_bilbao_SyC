package com.duoc.frontendS8.dto;

public record SolicitudAdopcionApiRequest(
		Long animalId,
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
		String motivacionAdopcion) {
}
