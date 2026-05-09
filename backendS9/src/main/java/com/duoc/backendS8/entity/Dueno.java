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
@Table(name = "duenos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dueno {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(nullable = false, length = 160)
	private String nombreCompleto;

	@Column(length = 160)
	private String email;

	@Column(length = 40)
	private String telefono;

	@Column(length = 255)
	private String direccion;
}
