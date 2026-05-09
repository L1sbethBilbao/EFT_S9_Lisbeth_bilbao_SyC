package com.duoc.backendS8.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;

import com.duoc.backendS8.dto.RegistroMedicoRequest;
import com.duoc.backendS8.dto.RegistroMedicoResponse;
import com.duoc.backendS8.entity.Cita;
import com.duoc.backendS8.entity.EstadoCita;
import com.duoc.backendS8.entity.RegistroMedico;
import com.duoc.backendS8.repository.CitaRepository;
import com.duoc.backendS8.repository.RegistroMedicoRepository;

@ExtendWith(MockitoExtension.class)
class RegistroMedicoServiceTest {

	@Mock
	private RegistroMedicoRepository registroMedicoRepository;
	@Mock
	private CitaRepository citaRepository;

	@InjectMocks
	private RegistroMedicoService registroMedicoService;

	@Test
	void listarVacio() {
		when(registroMedicoRepository.findAll()).thenReturn(List.of());
		assertThat(registroMedicoService.listar()).isEmpty();
	}

	@Test
	void crearActualizaCitaACompletada() {
		Cita cita = Cita.builder().id(3L).estado(EstadoCita.PROGRAMADA).build();
		when(citaRepository.findById(3L)).thenReturn(Optional.of(cita));
		when(registroMedicoRepository.findByCita_Id(3L)).thenReturn(Optional.empty());
		doAnswer(inv -> {
			RegistroMedico rm = inv.getArgument(0);
			rm.setId(100L);
			return rm;
		}).when(registroMedicoRepository).save(any(RegistroMedico.class));

		RegistroMedicoRequest req = new RegistroMedicoRequest(
				3L,
				LocalDateTime.now(),
				" resfriado ",
				" reposo ",
				null,
				null);
		RegistroMedicoResponse r = registroMedicoService.crear(req);
		assertThat(r.citaId()).isEqualTo(3L);
		assertThat(cita.getEstado()).isEqualTo(EstadoCita.COMPLETADA);
	}

	@Test
	void crearRechazaSiCitaNoProgramada() {
		Cita cita = Cita.builder().id(1L).estado(EstadoCita.COMPLETADA).build();
		when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
		RegistroMedicoRequest req = new RegistroMedicoRequest(1L, LocalDateTime.now(), "x", null, null, null);
		assertThatThrownBy(() -> registroMedicoService.crear(req)).isInstanceOf(IllegalStateException.class);
	}
}
