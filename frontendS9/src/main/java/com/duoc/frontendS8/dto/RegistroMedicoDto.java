package com.duoc.frontendS8.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RegistroMedicoDto(
		Long id,
		Long citaId,
		LocalDateTime fechaAtencion,
		String diagnostico,
		String tratamiento,
		String medicamentos,
		String notas,
		Long facturaId) {
}
