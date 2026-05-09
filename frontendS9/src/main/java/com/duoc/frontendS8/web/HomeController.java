package com.duoc.frontendS8.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

import com.duoc.frontendS8.dto.AnimalDto;
import com.duoc.frontendS8.service.BackendApiClient;
import com.duoc.frontendS8.validation.InputSafety;

@Controller
public class HomeController {

	private static final String FILTRO_SEGURIDAD_AVISO =
			"Un filtro contenía texto no permitido (etiquetas HTML o scripts) y fue omitido por seguridad.";

	private final BackendApiClient backendApiClient;

	public HomeController(BackendApiClient backendApiClient) {
		this.backendApiClient = backendApiClient;
	}

	@GetMapping("/")
	public String index(
			@RequestParam(required = false) String especie,
			@RequestParam(required = false) String raza,
			@RequestParam(required = false) String ubicacion,
			@RequestParam(required = false) Integer edadMin,
			@RequestParam(required = false) Integer edadMax,
			@RequestParam(required = false) String genero,
			@RequestParam(required = false) String estadoAdopcion,
			Model model) {
		boolean[] filtroPeligroso = { false };
		String especieQ = safeCatalogParam(especie, filtroPeligroso);
		String razaQ = safeCatalogParam(raza, filtroPeligroso);
		String ubicacionQ = safeCatalogParam(ubicacion, filtroPeligroso);
		String generoQ = safeCatalogParam(genero, filtroPeligroso);
		String estadoQ = safeCatalogParam(estadoAdopcion, filtroPeligroso);
		if (filtroPeligroso[0]) {
			model.addAttribute("filtroSeguridadAviso", FILTRO_SEGURIDAD_AVISO);
		}
		try {
			List<AnimalDto> animales = backendApiClient.listarAnimales(
					especieQ, razaQ, ubicacionQ, edadMin, edadMax, generoQ, estadoQ);
			model.addAttribute("animales", animales);
			model.addAttribute("filtroEspecie", especieQ);
			model.addAttribute("filtroRaza", razaQ);
			model.addAttribute("filtroUbicacion", ubicacionQ);
			model.addAttribute("filtroEdadMin", edadMin);
			model.addAttribute("filtroEdadMax", edadMax);
			model.addAttribute("filtroGenero", generoQ);
			model.addAttribute("filtroEstadoAdopcion", estadoQ);
		}
		catch (RestClientException e) {
			model.addAttribute("animales", List.<AnimalDto>of());
			model.addAttribute(
					"backendError",
					"No se pudo conectar con el API. Asegúrese de que el backend esté en ejecución (puerto 8080) y MySQL disponible.");
		}
		model.addAttribute("tituloCatalogo", "Catálogo de mascotas");
		model.addAttribute("soloDisponibles", false);
		model.addAttribute("filterFormAction", "/");
		return "index";
	}

	@GetMapping("/adopciones/disponibles")
	public String disponiblesParaAdopcion(
			@RequestParam(required = false) String especie,
			@RequestParam(required = false) String raza,
			@RequestParam(required = false) String ubicacion,
			@RequestParam(required = false) Integer edadMin,
			@RequestParam(required = false) Integer edadMax,
			@RequestParam(required = false) String genero,
			Model model) {
		boolean[] filtroPeligroso = { false };
		String especieQ = safeCatalogParam(especie, filtroPeligroso);
		String razaQ = safeCatalogParam(raza, filtroPeligroso);
		String ubicacionQ = safeCatalogParam(ubicacion, filtroPeligroso);
		String generoQ = safeCatalogParam(genero, filtroPeligroso);
		if (filtroPeligroso[0]) {
			model.addAttribute("filtroSeguridadAviso", FILTRO_SEGURIDAD_AVISO);
		}
		try {
			List<AnimalDto> animales = backendApiClient.listarAnimales(
					especieQ, razaQ, ubicacionQ, edadMin, edadMax, generoQ, "DISPONIBLE");
			model.addAttribute("animales", animales);
			model.addAttribute("filtroEspecie", especieQ);
			model.addAttribute("filtroRaza", razaQ);
			model.addAttribute("filtroUbicacion", ubicacionQ);
			model.addAttribute("filtroEdadMin", edadMin);
			model.addAttribute("filtroEdadMax", edadMax);
			model.addAttribute("filtroGenero", generoQ);
			model.addAttribute("filtroEstadoAdopcion", "DISPONIBLE");
		}
		catch (RestClientException e) {
			model.addAttribute("animales", List.<AnimalDto>of());
			model.addAttribute(
					"backendError",
					"No se pudo conectar con el API. Asegúrese de que el backend esté en ejecución (puerto 8080) y MySQL disponible.");
		}
		model.addAttribute("tituloCatalogo", "Mascotas disponibles para adopción");
		model.addAttribute("soloDisponibles", true);
		model.addAttribute("filterFormAction", "/adopciones/disponibles");
		return "index";
	}

	/**
	 * Devuelve el texto recortado para el API o {@code null}; si el cliente envió marcado peligroso, marca el flag y devuelve {@code null}.
	 */
	private static String safeCatalogParam(String raw, boolean[] anyDangerous) {
		if (raw == null || raw.isBlank()) {
			return null;
		}
		String t = raw.trim();
		if (InputSafety.containsDangerousMarkup(t)) {
			anyDangerous[0] = true;
			return null;
		}
		return t;
	}
}
