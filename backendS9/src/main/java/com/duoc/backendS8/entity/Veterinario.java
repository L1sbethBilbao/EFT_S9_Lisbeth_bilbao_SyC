package com.duoc.backendS8.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "veterinarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Veterinario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(nullable = false, length = 160)
	private String nombre;

	@Column(length = 120)
	private String especialidad;

	@Column(nullable = false)
	@Builder.Default
	private boolean activo = true;
}
