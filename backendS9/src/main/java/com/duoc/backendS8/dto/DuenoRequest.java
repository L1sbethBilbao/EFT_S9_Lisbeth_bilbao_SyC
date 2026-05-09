package com.duoc.backendS8.dto;

import jakarta.validation.constraints.NotBlank;

public record DuenoRequest(
		@NotBlank String nombreCompleto,
		String email,
		String telefono,
		String direccion) {
}
