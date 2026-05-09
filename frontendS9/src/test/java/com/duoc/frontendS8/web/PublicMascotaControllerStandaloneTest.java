package com.duoc.frontendS8.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClientException;

import com.duoc.frontendS8.dto.AnimalDto;
import com.duoc.frontendS8.service.BackendApiClient;

@ExtendWith(MockitoExtension.class)
class PublicMascotaControllerStandaloneTest {

	@Mock
	private BackendApiClient backendApiClient;

	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(new PublicMascotaController(backendApiClient)).build();
	}

	@Test
	void detalleOk() throws Exception {
		when(backendApiClient.obtenerAnimal(1L)).thenReturn(new AnimalDto(1L, "Rayo", null, null, 1, null, null, null, null, null, null));
		mockMvc.perform(get("/mascotas/1"))
				.andExpect(status().isOk())
				.andExpect(view().name("mascota-detalle"))
				.andExpect(model().attributeExists("mascota"));
	}

	@Test
	void detalleErrorBackend() throws Exception {
		when(backendApiClient.obtenerAnimal(2L)).thenThrow(new RestClientException("x"));
		mockMvc.perform(get("/mascotas/2"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("backendError"));
	}
}
