package com.duoc.backendS8.entity;

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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "animales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Animal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(nullable = false, length = 120)
	private String nombre;

	@Column(length = 120)
	private String especie;

	@Column(length = 120)
	private String raza;

	@Min(0)
	@Column(nullable = false)
	private Integer edad;

	@Column(length = 160)
	private String ubicacion;

	@Enumerated(EnumType.STRING)
	@Column(length = 24)
	private GeneroAnimal genero;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado_adopcion", length = 32)
	private EstadoAdopcion estadoAdopcion;

	@Column(name = "foto_url", length = 512)
	private String fotoUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dueno_id")
	private Dueno dueno;
}
