package com.duoc.frontendS8.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record FacturaForm(@NotNull Long registroMedicoId, @NotEmpty @Valid List<LineaFacturaForm> lineas) {
}
