package com.duoc.frontendS8.dto;

import com.duoc.frontendS8.validation.SafePlainText;

import jakarta.validation.constraints.NotBlank;

public record DuenoForm(
		@NotBlank @SafePlainText String nombreCompleto,
		String email,
		@SafePlainText String telefono,
		@SafePlainText String direccion) {
}
