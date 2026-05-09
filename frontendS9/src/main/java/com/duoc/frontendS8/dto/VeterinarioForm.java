package com.duoc.frontendS8.dto;

import jakarta.validation.constraints.NotBlank;

public record VeterinarioForm(@NotBlank String nombre, String especialidad, Boolean activo) {
}
