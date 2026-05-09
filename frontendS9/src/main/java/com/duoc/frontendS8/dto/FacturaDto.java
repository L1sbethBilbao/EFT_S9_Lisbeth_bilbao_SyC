package com.duoc.frontendS8.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FacturaDto(
		Long id,
		String numeroFactura,
		LocalDate fechaEmision,
		BigDecimal total,
		Long registroMedicoId,
		Long citaId,
		String animalNombre,
		List<LineaFacturaDto> lineas) {
}
