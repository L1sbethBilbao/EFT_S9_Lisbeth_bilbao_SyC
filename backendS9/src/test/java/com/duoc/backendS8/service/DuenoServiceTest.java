package com.duoc.backendS8.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;

import com.duoc.backendS8.dto.DuenoRequest;
import com.duoc.backendS8.dto.DuenoResponse;
import com.duoc.backendS8.entity.Dueno;
import com.duoc.backendS8.repository.DuenoRepository;

@ExtendWith(MockitoExtension.class)
class DuenoServiceTest {

	@Mock
	private DuenoRepository duenoRepository;

	@InjectMocks
	private DuenoService duenoService;

	@Test
	void listarMapsAll() {
		Dueno d = Dueno.builder().id(1L).nombreCompleto("Ana").email("a@x.cl").telefono("1").direccion("Santiago").build();
		when(duenoRepository.findAll()).thenReturn(List.of(d));
		assertThat(duenoService.listar()).hasSize(1).extracting(DuenoResponse::nombreCompleto).containsExactly("Ana");
	}

	@Test
	void obtenerFound() {
		Dueno d = Dueno.builder().id(2L).nombreCompleto("Bob").email(null).telefono(null).direccion(null).build();
		when(duenoRepository.findById(2L)).thenReturn(Optional.of(d));
		DuenoResponse r = duenoService.obtener(2L);
		assertThat(r.id()).isEqualTo(2L);
		assertThat(r.nombreCompleto()).isEqualTo("Bob");
	}

	@Test
	void obtenerNotFound() {
		when(duenoRepository.findById(99L)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> duenoService.obtener(99L)).isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void crearTrimsAndSaves() {
		doAnswer(inv -> {
			Dueno arg = inv.getArgument(0);
			arg.setId(5L);
			return arg;
		}).when(duenoRepository).save(any(Dueno.class));
		DuenoRequest req = new DuenoRequest("  Juan  ", "  j@e.com ", "", "  ");
		DuenoResponse r = duenoService.crear(req);
		assertThat(r.id()).isEqualTo(5L);
		assertThat(r.nombreCompleto()).isEqualTo("Juan");
		assertThat(r.email()).isEqualTo("j@e.com");
		verify(duenoRepository).save(any(Dueno.class));
	}

	@Test
	void eliminarWhenMissingThrows() {
		when(duenoRepository.existsById(1L)).thenReturn(false);
		assertThatThrownBy(() -> duenoService.eliminar(1L)).isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void eliminarDeletes() {
		when(duenoRepository.existsById(1L)).thenReturn(true);
		duenoService.eliminar(1L);
		verify(duenoRepository).deleteById(1L);
	}
}
