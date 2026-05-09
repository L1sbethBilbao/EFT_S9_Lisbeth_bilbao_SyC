package com.duoc.frontendS8.dto;

import com.duoc.frontendS8.validation.SafePlainText;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AnimalForm(
		@NotBlank(message = "El nombre es obligatorio") @SafePlainText String nombre,
		@SafePlainText String especie,
		@SafePlainText String raza,
		@Min(value = 0, message = "La edad no puede ser negativa") Integer edad,
		@SafePlainText String ubicacion,
		@SafePlainText String genero,
		@SafePlainText String estadoAdopcion,
		@SafePlainText String fotoUrl,
		Long duenoId) {
}
