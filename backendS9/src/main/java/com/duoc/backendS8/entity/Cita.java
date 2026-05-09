package com.duoc.backendS8.entity;

import java.time.LocalDateTime;

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
@Table(name = "citas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cita {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "animal_id", nullable = false)
	private Animal animal;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "veterinario_id", nullable = false)
	private Veterinario veterinario;

	@Column(nullable = false)
	private LocalDateTime fechaHora;

	@Column(length = 500)
	private String motivo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 24)
	private EstadoCita estado;
}
