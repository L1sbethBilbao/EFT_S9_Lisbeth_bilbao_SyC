package com.duoc.backendS8.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lineas_factura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LineaFactura {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "factura_id", nullable = false)
	private Factura factura;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 24)
	private TipoLineaFactura tipo;

	@Column(nullable = false, length = 500)
	private String descripcion;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal monto;
}
