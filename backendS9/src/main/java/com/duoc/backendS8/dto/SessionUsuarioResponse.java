package com.duoc.backendS8.dto;

public record SessionUsuarioResponse(
		String username,
		String rol,
		Long veterinarioId,
		String veterinarioNombre) {
}
