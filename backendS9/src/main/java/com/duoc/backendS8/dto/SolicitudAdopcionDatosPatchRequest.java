package com.duoc.backendS8.dto;

import com.duoc.backendS8.entity.TipoViviendaAdopcion;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Corrección de datos del formulario por coordinación, con constancia de que el solicitante pidió el cambio.
 */
public record SolicitudAdopcionDatosPatchRequest(
		@NotBlank @Size(max = 200) String nombreCompleto,
		@NotBlank @Email @Size(max = 255) String email,
		@Size(max = 40) String telefono,
		@Size(max = 300) String direccion,
		@Size(max = 120) String ciudad,
		@NotNull TipoViviendaAdopcion tipoVivienda,
		@NotNull @Min(1) Integer personasEnHogar,
		Boolean tieneNinos,
		Boolean tieneOtrasMascotas,
		@Size(max = 4000) String experienciaMascotas,
		@NotBlank @Size(max = 4000) String motivacionAdopcion,
		@NotBlank @Size(min = 10, max = 2000) String constanciaSolicitante) {
}
