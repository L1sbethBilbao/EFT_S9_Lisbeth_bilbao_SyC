package com.duoc.backendS8.dto;

import jakarta.validation.constraints.NotBlank;

public record VeterinarioRequest(@NotBlank String nombre, String especialidad, Boolean activo) {
}
