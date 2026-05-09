package com.duoc.frontendS8.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LineaFacturaDto(Long id, String tipo, String descripcion, BigDecimal monto) {
}
