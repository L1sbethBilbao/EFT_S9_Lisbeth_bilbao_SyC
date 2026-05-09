package com.duoc.frontendS8.web;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.duoc.frontendS8.dto.AdopcionSeguimientoForm;
import com.duoc.frontendS8.dto.AdopcionSolicitudForm;
import com.duoc.frontendS8.dto.SolicitudAdopcionApiRequest;
import com.duoc.frontendS8.dto.SolicitudAdopcionCreadaDto;
import com.duoc.frontendS8.service.BackendApiClient;
import com.duoc.frontendS8.validation.InputSafety;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/adopciones")
public class AdopcionPublicFlujoWebController {

	private final BackendApiClient backendApiClient;

	public AdopcionPublicFlujoWebController(BackendApiClient backendApiClient) {
		this.backendApiClient = backendApiClient;
	}

	@GetMapping("/solicitar")
	public String formularioSolicitud(@RequestParam Long animalId, Model model) {
		try {
			model.addAttribute("mascota", backendApiClient.obtenerAnimal(animalId));
		}
		catch (RestClientException e) {
			model.addAttribute("backendError", "No se pudo cargar la mascota.");
			return "adopcion-solicitud";
		}
		AdopcionSolicitudForm form = new AdopcionSolicitudForm();
		form.setAnimalId(animalId);
		model.addAttribute("adopcionForm", form);
		return "adopcion-solicitud";
	}

	@PostMapping("/solicitar")
	public String enviarSolicitud(
			@Valid @ModelAttribute("adopcionForm") AdopcionSolicitudForm form,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			try {
				model.addAttribute("mascota", backendApiClient.obtenerAnimal(form.getAnimalId()));
			}
			catch (RestClientException e) {
				model.addAttribute("backendError", "No se pudo cargar la mascota.");
			}
			return "adopcion-solicitud";
		}
		var api = new SolicitudAdopcionApiRequest(
				form.getAnimalId(),
				form.getNombreCompleto(),
				form.getEmail(),
				blankToNull(form.getTelefono()),
				blankToNull(form.getDireccion()),
				blankToNull(form.getCiudad()),
				form.getTipoVivienda(),
				form.getPersonasEnHogar(),
				form.isTieneNinos(),
				form.isTieneOtrasMascotas(),
				blankToNull(form.getExperienciaMascotas()),
				form.getMotivacionAdopcion());
		try {
			SolicitudAdopcionCreadaDto creada = backendApiClient.crearSolicitudAdopcionPublica(api);
			redirectAttributes.addFlashAttribute("adopcionCreada", creada);
			return "redirect:/adopciones/gracias";
		}
		catch (HttpClientErrorException.BadRequest e) {
			model.addAttribute("errorApi", mensajeBadRequestApi(e));
			try {
				model.addAttribute("mascota", backendApiClient.obtenerAnimal(form.getAnimalId()));
			}
			catch (RestClientException ignored) {
				model.addAttribute("backendError", "No se pudo cargar la mascota.");
			}
			return "adopcion-solicitud";
		}
		catch (RestClientException e) {
			model.addAttribute("errorApi", "No se pudo conectar con el servidor. ¿Está el backend (8080) en ejecución?");
			try {
				model.addAttribute("mascota", backendApiClient.obtenerAnimal(form.getAnimalId()));
			}
			catch (RestClientException ignored) {
				model.addAttribute("backendError", "No se pudo cargar la mascota.");
			}
			return "adopcion-solicitud";
		}
	}

	@GetMapping("/gracias")
	public String gracias(Model model) {
		if (!model.containsAttribute("adopcionCreada")) {
			return "redirect:/";
		}
		return "adopcion-gracias";
	}

	@GetMapping("/seguimiento")
	public String seguimientoForm(@RequestParam(required = false) String codigo, Model model) {
		String codigoSeguro = null;
		if (codigo != null && !codigo.isBlank()) {
			String t = codigo.trim();
			if (InputSafety.containsDangerousMarkup(t)) {
				model.addAttribute("consultaError",
						"El código de seguimiento no puede contener etiquetas HTML ni scripts.");
			}
			else {
				codigoSeguro = t;
			}
		}
		if (!model.containsAttribute("seguimientoForm")) {
			AdopcionSeguimientoForm form = new AdopcionSeguimientoForm();
			if (codigoSeguro != null) {
				form.setCodigoSeguimiento(codigoSeguro);
			}
			model.addAttribute("seguimientoForm", form);
		}
		if (codigoSeguro != null) {
			try {
				var vista = backendApiClient.consultarSeguimientoPublico(codigoSeguro);
				if (vista != null) {
					model.addAttribute("vista", vista);
				}
				else {
					model.addAttribute("consultaError",
							"No encontramos una solicitud con ese código. Verifique e intente de nuevo.");
				}
			}
			catch (RestClientException e) {
				model.addAttribute("consultaError",
						"No se pudo consultar el seguimiento. Compruebe que el servidor esté disponible.");
			}
		}
		return "adopcion-seguimiento";
	}

	/**
	 * Oculta la solicitud en pantalla (sin cerrar el trámite en el servidor). El solicitante puede volver a
	 * ingresar el código cuando lo necesite.
	 */
	@GetMapping("/seguimiento/salir")
	public String seguimientoSalir(RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("seguimientoCerradoInfo",
				"Ha cerrado la vista de su seguimiento. Nadie más que use este equipo verá el detalle aquí hasta que vuelva a ingresar el código.");
		return "redirect:/adopciones/seguimiento";
	}

	@PostMapping("/seguimiento/mensaje")
	public String seguimientoEnviar(
			@Valid @ModelAttribute("seguimientoForm") AdopcionSeguimientoForm form,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			agregarVistaSiCodigoValido(form.getCodigoSeguimiento(), model);
			return "adopcion-seguimiento";
		}
		try {
			backendApiClient.enviarMensajeSeguimientoAdopcion(form.getCodigoSeguimiento(), form.getCuerpo());
			redirectAttributes.addFlashAttribute("mensajeOk", "Su mensaje fue enviado a coordinación de adopciones.");
			String q = URLEncoder.encode(form.getCodigoSeguimiento().trim(), StandardCharsets.UTF_8);
			return "redirect:/adopciones/seguimiento?codigo=" + q;
		}
		catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				model.addAttribute("errorApi", "Código de seguimiento no encontrado.");
			}
			else {
				model.addAttribute("errorApi", "No se pudo registrar el mensaje (¿solicitud cerrada?).");
			}
			agregarVistaSiCodigoValido(form.getCodigoSeguimiento(), model);
			return "adopcion-seguimiento";
		}
		catch (RestClientException e) {
			model.addAttribute("errorApi", "No se pudo conectar con el servidor.");
			agregarVistaSiCodigoValido(form.getCodigoSeguimiento(), model);
			return "adopcion-seguimiento";
		}
	}

	private void agregarVistaSiCodigoValido(String codigo, Model model) {
		if (codigo == null || codigo.isBlank() || model.containsAttribute("vista")) {
			return;
		}
		try {
			var vista = backendApiClient.consultarSeguimientoPublico(codigo.trim());
			if (vista != null) {
				model.addAttribute("vista", vista);
			}
		}
		catch (RestClientException ignored) {
			// ya hay errorApi / consultaError en pantalla
		}
	}

	private static String mensajeBadRequestApi(HttpClientErrorException.BadRequest e) {
		try {
			String raw = e.getResponseBodyAsString(StandardCharsets.UTF_8);
			if (raw != null && !raw.isBlank()) {
				JsonNode root = new ObjectMapper().readTree(raw);
				if (root.hasNonNull("message")) {
					String m = root.get("message").asText();
					if (m != null && !m.isBlank()) {
						return m;
					}
				}
			}
		}
		catch (Exception ignored) {
			// mensaje genérico
		}
		return "Revise los datos enviados; el servidor no aceptó la solicitud.";
	}

	private static String blankToNull(String s) {
		if (s == null || s.isBlank()) {
			return null;
		}
		return s.trim();
	}
}
