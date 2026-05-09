package com.duoc.frontendS8.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistroPasoForm(
		@NotNull Long citaId,
		@NotNull LocalDate fecha,
		@NotNull LocalTime hora,
		@NotBlank String diagnostico,
		String tratamiento,
		String medicamentos,
		String notas) {
}
