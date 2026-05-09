package com.duoc.backendS8.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EntityPrePersistTest {

	@Test
	void solicitudAdopcion_prePersist_defaults() {
		SolicitudAdopcion s = SolicitudAdopcion.builder()
				.codigoSeguimiento("c")
				.animal(Animal.builder().id(1L).nombre("A").estadoAdopcion(EstadoAdopcion.DISPONIBLE).build())
				.nombreCompleto("N")
				.email("e@test.cl")
				.tipoVivienda(TipoViviendaAdopcion.CASA)
				.personasEnHogar(1)
				.motivacionAdopcion("m")
				.build();
		s.prePersist();
		assertThat(s.getCreatedAt()).isNotNull();
		assertThat(s.getEstado()).isEqualTo(EstadoSolicitudAdopcion.PENDIENTE);
		assertThat(s.getTieneNinos()).isFalse();
		assertThat(s.getTieneOtrasMascotas()).isFalse();
	}

	@Test
	void mensajeSolicitudAdopcion_prePersist_setsInstant() {
		MensajeSolicitudAdopcion m = MensajeSolicitudAdopcion.builder()
				.solicitud(SolicitudAdopcion.builder()
						.id(1L)
						.codigoSeguimiento("z")
						.animal(Animal.builder().id(2L).nombre("B").estadoAdopcion(EstadoAdopcion.DISPONIBLE).build())
						.nombreCompleto("N")
						.email("n@test.cl")
						.tipoVivienda(TipoViviendaAdopcion.CASA)
						.personasEnHogar(1)
						.motivacionAdopcion("m")
						.build())
				.rolAutor(RolMensajeSolicitudAdopcion.SOLICITANTE)
				.cuerpo("hola")
				.build();
		m.prePersist();
		assertThat(m.getCreatedAt()).isNotNull();
	}
}
