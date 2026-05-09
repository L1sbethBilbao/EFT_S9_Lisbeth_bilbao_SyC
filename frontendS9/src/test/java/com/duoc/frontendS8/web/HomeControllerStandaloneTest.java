package com.duoc.frontendS8.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

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
class HomeControllerStandaloneTest {

	@Mock
	private BackendApiClient backendApiClient;

	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(new HomeController(backendApiClient)).build();
	}

	@Test
	void indexCargaAnimales() throws Exception {
		var dto = new AnimalDto(1L, "Rayo", "perro", null, 2, null, null, null, null, null, null);
		when(backendApiClient.listarAnimales(null, null, null, null, null, null, null)).thenReturn(List.of(dto));
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name("index"))
				.andExpect(model().attribute("animales", List.of(dto)));
	}

	@Test
	void indexBackendErrorListaVacia() throws Exception {
		when(backendApiClient.listarAnimales(null, null, null, null, null, null, null))
				.thenThrow(new RestClientException("down"));
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("animales", List.of()))
				.andExpect(model().attributeExists("backendError"));
	}
}
