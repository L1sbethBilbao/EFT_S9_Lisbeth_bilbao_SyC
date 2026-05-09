package com.duoc.backendS8.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.duoc.backendS8.dto.AnimalResponse;
import com.duoc.backendS8.entity.EstadoAdopcion;
import com.duoc.backendS8.entity.GeneroAnimal;
import com.duoc.backendS8.service.AnimalService;

@ExtendWith(MockitoExtension.class)
class AnimalControllerStandaloneTest {

	@Mock
	private AnimalService animalService;

	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(new AnimalController(animalService)).build();
	}

	@Test
	void listarDevuelveJson() throws Exception {
		var dto = new AnimalResponse(1L, "Luna", "gato", null, 2, "Stgo", GeneroAnimal.FEMENINO, EstadoAdopcion.DISPONIBLE, null, null, null);
		when(animalService.buscar(null, null, null, null, null, null, null)).thenReturn(List.of(dto));
		mockMvc.perform(get("/api/animales").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].nombre").value("Luna"));
	}

	@Test
	void obtenerPorId() throws Exception {
		var dto = new AnimalResponse(2L, "Max", "perro", null, 4, null, GeneroAnimal.MASCULINO, EstadoAdopcion.DISPONIBLE, null, null, null);
		when(animalService.obtenerPorId(2L)).thenReturn(dto);
		mockMvc.perform(get("/api/animales/2")).andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Max"));
	}
}
