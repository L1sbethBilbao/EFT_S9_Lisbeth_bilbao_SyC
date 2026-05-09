package com.duoc.backendS8.dto;

import java.time.LocalDateTime;

public record RegistroMedicoResponse(
		Long id,
		Long citaId,
		LocalDateTime fechaAtencion,
		String diagnostico,
		String tratamiento,
		String medicamentos,
		String notas,
		Long facturaId) {
}
