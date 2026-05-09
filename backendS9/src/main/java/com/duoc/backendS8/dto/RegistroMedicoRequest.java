package com.duoc.backendS8.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistroMedicoRequest(
		@NotNull Long citaId,
		@NotNull LocalDateTime fechaAtencion,
		@NotBlank String diagnostico,
		String tratamiento,
		String medicamentos,
		String notas) {
}
