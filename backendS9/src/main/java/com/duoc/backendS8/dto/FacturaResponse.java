package com.duoc.backendS8.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record FacturaResponse(
		Long id,
		String numeroFactura,
		LocalDate fechaEmision,
		BigDecimal total,
		Long registroMedicoId,
		Long citaId,
		String animalNombre,
		List<LineaFacturaResponse> lineas) {
}
