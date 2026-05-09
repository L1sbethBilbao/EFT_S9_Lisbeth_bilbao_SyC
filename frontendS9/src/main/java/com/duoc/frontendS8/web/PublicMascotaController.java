package com.duoc.frontendS8.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClientException;

import com.duoc.frontendS8.service.BackendApiClient;

@Controller
public class PublicMascotaController {

	private final BackendApiClient backendApiClient;

	public PublicMascotaController(BackendApiClient backendApiClient) {
		this.backendApiClient = backendApiClient;
	}

	@GetMapping("/mascotas/{id}")
	public String detalle(@PathVariable Long id, Model model) {
		try {
			model.addAttribute("mascota", backendApiClient.obtenerAnimal(id));
		}
		catch (RestClientException e) {
			model.addAttribute("backendError", "No se pudo cargar la mascota.");
		}
		return "mascota-detalle";
	}
}
