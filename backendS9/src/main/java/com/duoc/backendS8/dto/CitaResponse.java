package com.duoc.backendS8.dto;

import java.time.LocalDateTime;

import com.duoc.backendS8.entity.EstadoCita;

public record CitaResponse(
		Long id,
		Long animalId,
		String animalNombre,
		Long veterinarioId,
		String veterinarioNombre,
		LocalDateTime fechaHora,
		String motivo,
		EstadoCita estado,
		boolean mascotaConHistorialClinico) {
}
