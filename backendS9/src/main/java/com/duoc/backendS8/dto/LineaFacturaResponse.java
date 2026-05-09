package com.duoc.backendS8.dto;

import java.math.BigDecimal;

import com.duoc.backendS8.entity.TipoLineaFactura;

public record LineaFacturaResponse(Long id, TipoLineaFactura tipo, String descripcion, BigDecimal monto) {
}
