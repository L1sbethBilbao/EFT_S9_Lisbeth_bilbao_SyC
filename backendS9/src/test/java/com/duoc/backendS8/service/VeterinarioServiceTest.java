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

import com.duoc.backendS8.dto.VeterinarioRequest;
import com.duoc.backendS8.dto.VeterinarioResponse;
import com.duoc.backendS8.entity.Veterinario;
import com.duoc.backendS8.repository.VeterinarioRepository;

@ExtendWith(MockitoExtension.class)
class VeterinarioServiceTest {

	@Mock
	private VeterinarioRepository veterinarioRepository;

	@InjectMocks
	private VeterinarioService veterinarioService;

	@Test
	void listarActivosIgnoresInactivos() {
		Veterinario a = Veterinario.builder().id(1L).nombre("Ana").especialidad("x").activo(true).build();
		Veterinario b = Veterinario.builder().id(2L).nombre("Bo").especialidad("y").activo(false).build();
		when(veterinarioRepository.findAll()).thenReturn(List.of(a, b));
		assertThat(veterinarioService.listarActivos()).extracting(VeterinarioResponse::id).containsExactly(1L);
	}

	@Test
	void listarTodos() {
		Veterinario a = Veterinario.builder().id(1L).nombre("Ana").especialidad("x").activo(true).build();
		when(veterinarioRepository.findAll()).thenReturn(List.of(a));
		assertThat(veterinarioService.listarTodos()).hasSize(1);
	}

	@Test
	void obtenerNotFound() {
		when(veterinarioRepository.findById(9L)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> veterinarioService.obtener(9L)).isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void crearActivoPorDefectoCuandoNull() {
		doAnswer(inv -> {
			Veterinario v = inv.getArgument(0);
			v.setId(5L);
			return v;
		}).when(veterinarioRepository).save(any(Veterinario.class));
		VeterinarioResponse r = veterinarioService.crear(new VeterinarioRequest("  Lu  ", "  cir  ", null));
		assertThat(r.nombre()).isEqualTo("Lu");
		assertThat(r.especialidad()).isEqualTo("cir");
		assertThat(r.activo()).isTrue();
	}

	@Test
	void eliminarDeletes() {
		when(veterinarioRepository.existsById(1L)).thenReturn(true);
		veterinarioService.eliminar(1L);
		verify(veterinarioRepository).deleteById(1L);
	}
}
