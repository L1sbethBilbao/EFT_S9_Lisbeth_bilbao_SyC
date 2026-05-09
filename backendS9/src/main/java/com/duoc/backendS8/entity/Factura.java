package com.duoc.backendS8.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "facturas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Factura {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "registro_medico_id", nullable = false, unique = true)
	private RegistroMedico registroMedico;

	@Column(name = "numero_factura", nullable = false, unique = true, length = 40)
	private String numeroFactura;

	@Column(name = "fecha_emision", nullable = false)
	private LocalDate fechaEmision;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal total;

	@OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<LineaFactura> lineas = new ArrayList<>();
}
