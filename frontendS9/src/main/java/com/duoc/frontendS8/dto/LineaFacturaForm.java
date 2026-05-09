package com.duoc.frontendS8.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LineaFacturaForm(@NotNull String tipo, @NotBlank String descripcion, @NotNull BigDecimal monto) {
}
