package com.duoc.backendS8.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.EntityNotFoundException;

import com.duoc.backendS8.dto.AnimalRequest;
import com.duoc.backendS8.dto.AnimalResponse;
import com.duoc.backendS8.entity.Animal;
import com.duoc.backendS8.entity.Dueno;
import com.duoc.backendS8.entity.EstadoAdopcion;
import com.duoc.backendS8.entity.GeneroAnimal;
import com.duoc.backendS8.repository.AnimalRepository;
import com.duoc.backendS8.repository.DuenoRepository;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

	@Mock
	private AnimalRepository animalRepository;
	@Mock
	private DuenoRepository duenoRepository;

	@InjectMocks
	private AnimalService animalService;

	@Test
	void buscarDelegatesToRepository() {
		Animal a = Animal.builder().id(1L).nombre("Luna").edad(3).genero(GeneroAnimal.FEMENINO).estadoAdopcion(EstadoAdopcion.DISPONIBLE).build();
		when(animalRepository.findAll(any(Specification.class), eq(Sort.by("nombre")))).thenReturn(List.of(a));
		List<AnimalResponse> res = animalService.buscar("gato", null, null, null, null, null, null);
		assertThat(res).hasSize(1);
		assertThat(res.get(0).nombre()).isEqualTo("Luna");
	}

	@Test
	void obtenerPorIdFound() {
		Animal a = Animal.builder().id(2L).nombre("Max").edad(5).build();
		when(animalRepository.findById(2L)).thenReturn(Optional.of(a));
		assertThat(animalService.obtenerPorId(2L).nombre()).isEqualTo("Max");
	}

	@Test
	void obtenerPorIdMissing() {
		when(animalRepository.findById(9L)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> animalService.obtenerPorId(9L)).isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void crearSinDueno() {
		AnimalRequest req = new AnimalRequest("Rex", "perro", null, 2, "Stgo", GeneroAnimal.MASCULINO, EstadoAdopcion.DISPONIBLE, null, null);
		doAnswer(inv -> {
			Animal x = inv.getArgument(0);
			x.setId(10L);
			return x;
		}).when(animalRepository).save(any(Animal.class));
		AnimalResponse r = animalService.crear(req);
		assertThat(r.id()).isEqualTo(10L);
		assertThat(r.duenoId()).isNull();
	}

	@Test
	void crearConDueno() {
		Dueno d = Dueno.builder().id(3L).nombreCompleto("Pepe").build();
		when(duenoRepository.findById(3L)).thenReturn(Optional.of(d));
		doAnswer(inv -> {
			Animal x = inv.getArgument(0);
			x.setId(11L);
			return x;
		}).when(animalRepository).save(any(Animal.class));
		AnimalRequest req = new AnimalRequest("Kiwi", "ave", null, 1, null, GeneroAnimal.FEMENINO, EstadoAdopcion.DISPONIBLE, null, 3L);
		AnimalResponse r = animalService.crear(req);
		assertThat(r.duenoId()).isEqualTo(3L);
		assertThat(r.duenoNombre()).isEqualTo("Pepe");
	}

	@Test
	void eliminarThrowsWhenMissing() {
		when(animalRepository.existsById(1L)).thenReturn(false);
		assertThatThrownBy(() -> animalService.eliminar(1L)).isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void eliminarOk() {
		when(animalRepository.existsById(1L)).thenReturn(true);
		animalService.eliminar(1L);
		verify(animalRepository).deleteById(1L);
	}
}
