package com.duoc.frontendS8.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SessionUsuarioDto(
		String username,
		String rol,
		Long veterinarioId,
		String veterinarioNombre) {
}
