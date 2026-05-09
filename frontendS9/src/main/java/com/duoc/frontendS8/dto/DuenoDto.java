package com.duoc.frontendS8.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DuenoDto(Long id, String nombreCompleto, String email, String telefono, String direccion) {
}
