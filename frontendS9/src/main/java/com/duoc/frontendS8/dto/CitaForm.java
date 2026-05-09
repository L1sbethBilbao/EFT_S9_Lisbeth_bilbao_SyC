package com.duoc.frontendS8.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record CitaForm(
		@NotNull Long animalId,
		@NotNull Long veterinarioId,
		@NotNull LocalDateTime fechaHora,
		String motivo) {
}
