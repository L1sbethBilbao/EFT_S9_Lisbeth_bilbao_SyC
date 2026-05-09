package com.duoc.backendS8.dto;

import java.util.List;

import com.duoc.backendS8.entity.EstadoSolicitudAdopcion;

public record SolicitudAdopcionSeguimientoPublicoResponse(
		String codigoSeguimiento,
		EstadoSolicitudAdopcion estado,
		String estadoDescripcion,
		String animalNombre,
		List<MensajeSeguimientoPublicoResponse> mensajes) {
}
