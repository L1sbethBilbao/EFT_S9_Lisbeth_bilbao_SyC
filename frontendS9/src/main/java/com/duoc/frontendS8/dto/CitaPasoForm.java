package com.duoc.frontendS8.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

/**
 * Formulario web (fecha y hora separadas para HTML5).
 */
public record CitaPasoForm(
		@NotNull Long animalId,
		@NotNull Long veterinarioId,
		@NotNull LocalDate fecha,
		@NotNull LocalTime hora,
		String motivo) {
}
