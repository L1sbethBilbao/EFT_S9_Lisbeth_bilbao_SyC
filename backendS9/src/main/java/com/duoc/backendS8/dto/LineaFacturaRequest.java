package com.duoc.backendS8.dto;

import java.math.BigDecimal;

import com.duoc.backendS8.entity.TipoLineaFactura;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LineaFacturaRequest(
		@NotNull TipoLineaFactura tipo,
		@NotBlank String descripcion,
		@NotNull BigDecimal monto) {
}
