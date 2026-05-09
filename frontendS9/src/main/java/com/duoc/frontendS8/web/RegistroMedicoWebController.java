package com.duoc.frontendS8.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.duoc.frontendS8.dto.CitaDto;
import com.duoc.frontendS8.dto.RegistroMedicoForm;
import com.duoc.frontendS8.dto.RegistroPasoForm;
import com.duoc.frontendS8.service.BackendApiClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/registros-medicos")
public class RegistroMedicoWebController {

	private final BackendApiClient backendApiClient;

	public RegistroMedicoWebController(BackendApiClient backendApiClient) {
		this.backendApiClient = backendApiClient;
	}

	@GetMapping
	public String listar(Model model, HttpServletRequest request) {
		model.addAttribute("registros", backendApiClient.listarRegistrosMedicos(SessionJwt.get(request)));
		return "registros-medicos";
	}

	@GetMapping("/nuevo")
	public String nuevo(
			@RequestParam Long citaId,
			Model model,
			HttpServletRequest request,
			RedirectAttributes ra) {
		try {
			CitaDto cita = backendApiClient.obtenerCita(SessionJwt.get(request), citaId);
			if (cita == null || cita.fechaHora() == null) {
				ra.addFlashAttribute("backendError", "No se pudo cargar la fecha de la cita.");
				return "redirect:/citas";
			}
			var fh = cita.fechaHora();
			var hora = fh.toLocalTime().withSecond(0).withNano(0);
			model.addAttribute("registroForm", new RegistroPasoForm(citaId, fh.toLocalDate(), hora, "", "", "", ""));
			model.addAttribute("fechaHoraCita", fh);
			return "registro-form";
		}
		catch (RestClientException e) {
			ra.addFlashAttribute("backendError", "No se pudo cargar la cita. ¿Tienes permiso o sigue programada?");
			return "redirect:/citas";
		}
	}

	@PostMapping
	public String crear(
			@Valid @ModelAttribute("registroForm") RegistroPasoForm form,
			BindingResult bindingResult,
			Model model,
			HttpServletRequest request,
			RedirectAttributes ra) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("fechaHoraCita", LocalDateTime.of(form.fecha(), form.hora()));
			return "registro-form";
		}
		LocalDateTime fh = LocalDateTime.of(form.fecha(), form.hora());
		RegistroMedicoForm api = new RegistroMedicoForm(
				form.citaId(),
				fh,
				form.diagnostico(),
				form.tratamiento(),
				form.medicamentos(),
				form.notas());
		backendApiClient.crearRegistroMedico(SessionJwt.get(request), api);
		ra.addFlashAttribute("mensaje", "Ficha de atención registrada.");
		return "redirect:/registros-medicos";
	}
}
