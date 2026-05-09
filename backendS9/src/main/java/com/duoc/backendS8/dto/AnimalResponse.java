package com.duoc.backendS8.dto;

import com.duoc.backendS8.entity.EstadoAdopcion;
import com.duoc.backendS8.entity.GeneroAnimal;

public record AnimalResponse(
		Long id,
		String nombre,
		String especie,
		String raza,
		Integer edad,
		String ubicacion,
		GeneroAnimal genero,
		EstadoAdopcion estadoAdopcion,
		String fotoUrl,
		Long duenoId,
		String duenoNombre) {
}
