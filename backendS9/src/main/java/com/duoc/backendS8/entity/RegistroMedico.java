package com.duoc.backendS8.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "registros_medicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroMedico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cita_id", nullable = false, unique = true)
	private Cita cita;

	@Column(nullable = false)
	private LocalDateTime fechaAtencion;

	@Column(length = 2000)
	private String diagnostico;

	@Column(length = 2000)
	private String tratamiento;

	@Column(length = 2000)
	private String medicamentos;

	@Column(length = 4000)
	private String notas;

	@OneToOne(mappedBy = "registroMedico")
	private Factura factura;
}
