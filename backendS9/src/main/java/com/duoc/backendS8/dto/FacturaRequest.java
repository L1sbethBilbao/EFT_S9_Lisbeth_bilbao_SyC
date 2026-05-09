package com.duoc.backendS8.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record FacturaRequest(@NotNull Long registroMedicoId, @NotEmpty @Valid List<LineaFacturaRequest> lineas) {
}
