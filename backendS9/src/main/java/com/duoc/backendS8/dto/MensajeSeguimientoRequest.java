package com.duoc.backendS8.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MensajeSeguimientoRequest(
		@NotBlank @Size(max = 40) String codigoSeguimiento,
		@NotBlank @Size(max = 4000) String cuerpo) {
}
