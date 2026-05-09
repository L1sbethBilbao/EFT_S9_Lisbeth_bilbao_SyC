package com.duoc.backendS8.dto;

import java.time.Instant;
import java.util.List;

import com.duoc.backendS8.entity.EstadoSolicitudAdopcion;
import com.duoc.backendS8.entity.TipoViviendaAdopcion;

public record SolicitudAdopcionDetalleResponse(
		Long id,
		String codigoSeguimiento,
		Long animalId,
		String animalNombre,
		String nombreCompleto,
		String email,
		String telefono,
		String direccion,
		String ciudad,
		TipoViviendaAdopcion tipoVivienda,
		Integer personasEnHogar,
		Boolean tieneNinos,
		Boolean tieneOtrasMascotas,
		String experienciaMascotas,
		String motivacionAdopcion,
		EstadoSolicitudAdopcion estado,
		Instant createdAt,
		List<MensajeSolicitudResponse> mensajes) {
}
