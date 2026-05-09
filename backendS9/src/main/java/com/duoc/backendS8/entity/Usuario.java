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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 64)
	private String username;

	@Column(nullable = false, length = 120)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(length = 32)
	private RolUsuario rol;

	/** Perfil clínico cuando {@link RolUsuario#VETERINARIO}; opcional. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "veterinario_id")
	private Veterinario veterinario;
}

