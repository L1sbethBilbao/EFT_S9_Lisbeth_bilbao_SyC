package com.duoc.backendS8.entity;

public enum RolUsuario {
	/** Acceso a todas las APIs privadas (gestión integral). */
	COORDINADOR,
	/** Citas, fichas médicas, facturas y veterinarios. */
	VETERINARIO,
	/** Dueños y mascotas (alta/edición/baja del catálogo). */
	GESTOR
}
