package com.duoc.backendS8.dto;

import java.time.Instant;

import com.duoc.backendS8.entity.RolMensajeSolicitudAdopcion;

public record MensajeSolicitudResponse(
		Long id,
		RolMensajeSolicitudAdopcion rolAutor,
		String cuerpo,
		Instant createdAt) {
}
