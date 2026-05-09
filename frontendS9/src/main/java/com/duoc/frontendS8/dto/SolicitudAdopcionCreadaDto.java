package com.duoc.frontendS8.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SolicitudAdopcionCreadaDto(
		Long id,
		String codigoSeguimiento,
		String animalNombre,
		String mensaje) implements Serializable {
	private static final long serialVersionUID = 1L;
}
