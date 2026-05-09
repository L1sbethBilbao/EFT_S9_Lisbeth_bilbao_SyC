package com.duoc.frontendS8.web;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.duoc.frontendS8.dto.MensajeCoordinadorForm;
import com.duoc.frontendS8.dto.SolicitudAdopcionDetalleDto;
import com.duoc.frontendS8.dto.SolicitudDatosPatchApiRequest;
import com.duoc.frontendS8.dto.SolicitudEdicionCoordinadorForm;
import com.duoc.frontendS8.service.BackendApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/adopciones/gestion")
public class AdopcionGestionWebController {

	private final BackendApiClient backendApiClient;

	public AdopcionGestionWebController(BackendApiClient backendApiClient) {
		this.backendApiClient = backendApiClient;
	}

	@GetMapping
	public String listar(Model model, HttpServletRequest request) {
		try {
			model.addAttribute("solicitudes", backendApiClient.listarSolicitudesAdopcion(SessionJwt.get(request)));
		}
		catch (RestClientException e) {
			model.addAttribute("backendError", "No se pudo cargar el listado de solicitudes.");
			model.addAttribute("solicitudes", java.util.List.of());
		}
		return "adopcion-gestion";
	}

	@GetMapping("/{id}")
	public String detalle(@PathVariable Long id, Model model, HttpServletRequest request) {
		try {
			SolicitudAdopcionDetalleDto sol = backendApiClient.obtenerSolicitudAdopcion(SessionJwt.get(request), id);
			model.addAttribute("solicitud", sol);
			if (!model.containsAttribute("solicitudEdicionForm")) {
				model.addAttribute("solicitudEdicionForm", formEdicionDesde(sol));
			}
		}
		catch (RestClientException e) {
			model.addAttribute("backendError", "No se pudo cargar la solicitud.");
			return "redirect:/adopciones/gestion";
		}
		model.addAttribute("mensajeForm", new MensajeCoordinadorForm());
		return "adopcion-gestion-detalle";
	}

	@PostMapping("/{id}/mensaje")
	public String enviarMensaje(
			@PathVariable Long id,
			@Valid @ModelAttribute("mensajeForm") MensajeCoordinadorForm form,
			BindingResult bindingResult,
			HttpServletRequest request) {
		if (bindingResult.hasErrors()) {
			return "redirect:/adopciones/gestion/" + id;
		}
		backendApiClient.coordinadorEnviarMensajeAdopcion(SessionJwt.get(request), id, form.getCuerpo());
		return "redirect:/adopciones/gestion/" + id;
	}

	@PostMapping("/{id}/estado")
	public String cambiarEstado(
			@PathVariable Long id,
			@RequestParam String estado,
			HttpServletRequest request) {
		backendApiClient.coordinadorActualizarEstadoSolicitud(SessionJwt.get(request), id, estado);
		return "redirect:/adopciones/gestion/" + id;
	}

	@PostMapping("/{id}/datos")
	public String actualizarDatosSolicitud(
			@PathVariable Long id,
			@Valid @ModelAttribute("solicitudEdicionForm") SolicitudEdicionCoordinadorForm form,
			BindingResult bindingResult,
			Model model,
			HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return prepararDetalleConError(model, request, id, form);
		}
		var api = new SolicitudDatosPatchApiRequest(
				form.getNombreCompleto().trim(),
				form.getEmail().trim(),
				blankToNull(form.getTelefono()),
				blankToNull(form.getDireccion()),
				blankToNull(form.getCiudad()),
				form.getTipoVivienda().trim(),
				form.getPersonasEnHogar(),
				form.isTieneNinos(),
				form.isTieneOtrasMascotas(),
				blankToNull(form.getExperienciaMascotas()),
				form.getMotivacionAdopcion().trim(),
				form.getConstanciaSolicitante().trim());
		try {
			backendApiClient.coordinadorActualizarDatosSolicitud(SessionJwt.get(request), id, api);
			redirectAttributes.addFlashAttribute("mensaje",
					"Datos de la solicitud actualizados. El registro quedó en la conversación para la persona que adopta.");
			return "redirect:/adopciones/gestion/" + id;
		}
		catch (HttpClientErrorException.BadRequest e) {
			model.addAttribute("errorApi", mensajeBadRequestApi(e));
			return prepararDetalleConError(model, request, id, form);
		}
		catch (RestClientException e) {
			model.addAttribute("errorApi", "No se pudo guardar los cambios. Verifique la sesión o que el backend esté en ejecución.");
			return prepararDetalleConError(model, request, id, form);
		}
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(
			@PathVariable Long id,
			HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		try {
			backendApiClient.eliminarSolicitudAdopcion(SessionJwt.get(request), id);
			redirectAttributes.addFlashAttribute("mensaje", "La solicitud de adopción fue eliminada.");
		}
		catch (RestClientException e) {
			redirectAttributes.addFlashAttribute("backendError", "No se pudo eliminar la solicitud. Intente de nuevo o verifique la sesión.");
		}
		return "redirect:/adopciones/gestion";
	}

	private String prepararDetalleConError(
			Model model,
			HttpServletRequest request,
			Long id,
			SolicitudEdicionCoordinadorForm form) {
		try {
			model.addAttribute("solicitud", backendApiClient.obtenerSolicitudAdopcion(SessionJwt.get(request), id));
		}
		catch (RestClientException e) {
			model.addAttribute("backendError", "No se pudo recargar la solicitud.");
		}
		model.addAttribute("solicitudEdicionForm", form);
		model.addAttribute("mensajeForm", new MensajeCoordinadorForm());
		return "adopcion-gestion-detalle";
	}

	private static SolicitudEdicionCoordinadorForm formEdicionDesde(SolicitudAdopcionDetalleDto s) {
		var f = new SolicitudEdicionCoordinadorForm();
		f.setNombreCompleto(s.nombreCompleto());
		f.setEmail(s.email());
		f.setTelefono(s.telefono() != null ? s.telefono() : "");
		f.setDireccion(s.direccion() != null ? s.direccion() : "");
		f.setCiudad(s.ciudad() != null ? s.ciudad() : "");
		f.setTipoVivienda(s.tipoVivienda() != null ? s.tipoVivienda() : "CASA");
		f.setPersonasEnHogar(s.personasEnHogar());
		f.setTieneNinos(Boolean.TRUE.equals(s.tieneNinos()));
		f.setTieneOtrasMascotas(Boolean.TRUE.equals(s.tieneOtrasMascotas()));
		f.setExperienciaMascotas(s.experienciaMascotas() != null ? s.experienciaMascotas() : "");
		f.setMotivacionAdopcion(s.motivacionAdopcion());
		f.setConstanciaSolicitante("");
		return f;
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
			// mensaje genérico abajo
		}
		return "Revise los datos enviados; el servidor no aceptó la actualización.";
	}

	private static String blankToNull(String s) {
		if (s == null || s.isBlank()) {
			return null;
		}
		return s.trim();
	}
}
