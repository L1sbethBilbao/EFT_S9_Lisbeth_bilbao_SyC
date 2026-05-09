package com.duoc.backendS8.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class DtoRecordsTest {

	@Test
	void sessionUsuarioResponse() {
		var s = new SessionUsuarioResponse("u", "COORDINADOR", 1L, "Dr");
		assertThat(s.username()).isEqualTo("u");
		assertThat(s.veterinarioId()).isEqualTo(1L);
	}

	@Test
	void citaRequest() {
		var t = LocalDateTime.now();
		var c = new CitaRequest(1L, 2L, t, "motivo");
		assertThat(c.animalId()).isEqualTo(1L);
		assertThat(c.motivo()).isEqualTo("motivo");
	}

	@Test
	void enviarFacturaRequest() {
		assertThat(new EnviarFacturaRequest("a@b.cl").email()).isEqualTo("a@b.cl");
	}
}
