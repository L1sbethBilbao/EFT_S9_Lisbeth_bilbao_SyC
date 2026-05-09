package com.duoc.frontendS8.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CitaDto(
		Long id,
		Long animalId,
		String animalNombre,
		Long veterinarioId,
		String veterinarioNombre,
		LocalDateTime fechaHora,
		String motivo,
		String estado,
		Boolean mascotaConHistorialClinico) {
}
