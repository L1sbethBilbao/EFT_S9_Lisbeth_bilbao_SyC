package com.duoc.backendS8.dto;

import com.duoc.backendS8.entity.EstadoAdopcion;
import com.duoc.backendS8.entity.GeneroAnimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AnimalRequest(
		@NotBlank String nombre,
		String especie,
		String raza,
		@Min(0) Integer edad,
		String ubicacion,
		GeneroAnimal genero,
		EstadoAdopcion estadoAdopcion,
		String fotoUrl,
		Long duenoId) {
}
