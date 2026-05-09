package com.duoc.frontendS8.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AnimalDto(
		Long id,
		String nombre,
		String especie,
		String raza,
		Integer edad,
		String ubicacion,
		String genero,
		String estadoAdopcion,
		String fotoUrl,
		Long duenoId,
		String duenoNombre) {
}
