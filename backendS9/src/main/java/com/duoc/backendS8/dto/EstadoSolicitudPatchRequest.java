package com.duoc.backendS8.dto;

import com.duoc.backendS8.entity.EstadoSolicitudAdopcion;

import jakarta.validation.constraints.NotNull;

public record EstadoSolicitudPatchRequest(
		@NotNull EstadoSolicitudAdopcion estado) {
}
