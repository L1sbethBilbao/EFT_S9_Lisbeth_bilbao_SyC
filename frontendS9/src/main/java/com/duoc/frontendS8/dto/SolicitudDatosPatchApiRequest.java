package com.duoc.frontendS8.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SolicitudDatosPatchApiRequest(
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
		String constanciaSolicitante) {
}
