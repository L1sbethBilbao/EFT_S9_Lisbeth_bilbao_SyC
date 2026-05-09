package com.duoc.backendS8.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record CitaRequest(
		@NotNull Long animalId,
		@NotNull Long veterinarioId,
		@NotNull LocalDateTime fechaHora,
		String motivo) {
}
