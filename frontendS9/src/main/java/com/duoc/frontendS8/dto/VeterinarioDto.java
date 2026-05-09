package com.duoc.frontendS8.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VeterinarioDto(Long id, String nombre, String especialidad, boolean activo) {
}
