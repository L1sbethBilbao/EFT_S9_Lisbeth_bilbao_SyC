package com.duoc.frontendS8.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MensajeSeguimientoVistaDto(
		String rolAutor,
		String cuerpo,
		Instant createdAt) {
}
