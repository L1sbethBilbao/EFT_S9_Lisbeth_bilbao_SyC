package com.duoc.frontendS8.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SolicitudSeguimientoVistaDto(
		String codigoSeguimiento,
		String estado,
		String estadoDescripcion,
		String animalNombre,
		List<MensajeSeguimientoVistaDto> mensajes) {
}
