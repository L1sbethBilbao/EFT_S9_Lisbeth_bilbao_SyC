package com.duoc.backendS8.entity;

import java.time.Instant;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "solicitudes_adopcion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudAdopcion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "codigo_seguimiento", nullable = false, unique = true, length = 40)
	private String codigoSeguimiento;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "animal_id", nullable = false)
	private Animal animal;

	@Column(name = "nombre_completo", nullable = false, length = 200)
	private String nombreCompleto;

	@Column(nullable = false, length = 255)
	private String email;

	@Column(length = 40)
	private String telefono;

	@Column(length = 300)
	private String direccion;

	@Column(length = 120)
	private String ciudad;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_vivienda", nullable = false, length = 24)
	private TipoViviendaAdopcion tipoVivienda;

	@Column(name = "personas_hogar", nullable = false)
	private Integer personasEnHogar;

	@Column(name = "tiene_ninos")
	private Boolean tieneNinos;

	@Column(name = "tiene_otras_mascotas")
	private Boolean tieneOtrasMascotas;

	@Column(name = "experiencia_mascotas", length = 4000)
	private String experienciaMascotas;

	@Column(name = "motivacion_adopcion", nullable = false, length = 4000)
	private String motivacionAdopcion;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 24)
	private EstadoSolicitudAdopcion estado;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@PrePersist
	void prePersist() {
		if (createdAt == null) {
			createdAt = Instant.now();
		}
		if (estado == null) {
			estado = EstadoSolicitudAdopcion.PENDIENTE;
		}
		if (tieneNinos == null) {
			tieneNinos = false;
		}
		if (tieneOtrasMascotas == null) {
			tieneOtrasMascotas = false;
		}
	}
}
