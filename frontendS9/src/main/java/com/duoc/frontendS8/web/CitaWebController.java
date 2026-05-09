package com.duoc.frontendS8.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.duoc.frontendS8.dto.AnimalDto;
import com.duoc.frontendS8.dto.CitaForm;
import com.duoc.frontendS8.dto.CitaPasoForm;
import com.duoc.frontendS8.dto.SessionUsuarioDto;
import com.duoc.frontendS8.dto.VeterinarioDto;
import com.duoc.frontendS8.service.BackendApiClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/citas")
public class CitaWebController {

	private final BackendApiClient backendApiClient;

	public CitaWebController(BackendApiClient backendApiClient) {
		this.backendApiClient = backendApiClient;
	}

	@GetMapping
	public String listar(Model model, HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		model.addAttribute("citas", backendApiClient.listarCitas(SessionJwt.get(request)));
		return "citas";
	}

	@GetMapping("/nueva")
	public String nueva(Model model, HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		String jwt = SessionJwt.get(request);
		SessionUsuarioDto me = backendApiClient.obtenerSesion(jwt);
		List<AnimalDto> animales = backendApiClient.listarAnimales(null, null, null, null, null, null, null);
		Long animalId = animales.isEmpty() ? null : animales.get(0).id();
		LocalDate d = LocalDate.now().plusDays(1);
		LocalTime t = LocalTime.of(10, 0);
		boolean elegirVet = "COORDINADOR".equals(me.rol());
		if (elegirVet) {
			model.addAttribute("citaForm", new CitaPasoForm(animalId, null, d, t, ""));
		}
		else {
			if (me.veterinarioId() == null) {
				model.addAttribute(
						"backendError",
						"Tu usuario veterinario no tiene un perfil clínico vinculado. Contacta a la coordinación.");
			}
			model.addAttribute("citaForm", new CitaPasoForm(animalId, me.veterinarioId(), d, t, ""));
		}
		model.addAttribute("elegirVeterinario", elegirVet);
		model.addAttribute("miVeterinarioNombre", me.veterinarioNombre() != null ? me.veterinarioNombre() : "");
		model.addAttribute("animales", animales);
		model.addAttribute("veterinarios", veterinariosActivosParaAgenda(jwt));
		return "cita-form";
	}

	@PostMapping
	public String crear(
			@Valid @ModelAttribute("citaForm") CitaPasoForm form,
			BindingResult bindingResult,
			Model model,
			HttpServletRequest request,
			RedirectAttributes ra) {
		String jwt = SessionJwt.get(request);
		if (bindingResult.hasErrors()) {
			rellenarModeloFormularioCita(model, jwt, form);
			return "cita-form";
		}
		LocalDateTime fh = LocalDateTime.of(form.fecha(), form.hora());
		CitaForm api = new CitaForm(form.animalId(), form.veterinarioId(), fh, form.motivo());
		backendApiClient.crearCita(jwt, api);
		ra.addFlashAttribute("mensaje", "Cita agendada.");
		return "redirect:/citas";
	}

	private void rellenarModeloFormularioCita(Model model, String jwt, CitaPasoForm formActual) {
		SessionUsuarioDto me = backendApiClient.obtenerSesion(jwt);
		model.addAttribute("citaForm", formActual);
		model.addAttribute("elegirVeterinario", "COORDINADOR".equals(me.rol()));
		model.addAttribute("miVeterinarioNombre", me.veterinarioNombre() != null ? me.veterinarioNombre() : "");
		model.addAttribute("animales", backendApiClient.listarAnimales(null, null, null, null, null, null, null));
		model.addAttribute("veterinarios", veterinariosActivosParaAgenda(jwt));
	}

	/** Veterinarios activos para el combo: mismo listado que /veterinarios, filtrado. */
	private List<VeterinarioDto> veterinariosActivosParaAgenda(String jwt) {
		return backendApiClient.listarVeterinarios(jwt).stream()
				.filter(VeterinarioDto::activo)
				.sorted(Comparator.comparing(VeterinarioDto::nombre, Comparator.nullsLast(String::compareToIgnoreCase)))
				.toList();
	}

	@PostMapping("/{id}/cancelar")
	public String cancelar(@PathVariable Long id, HttpServletRequest request, RedirectAttributes ra) {
		backendApiClient.cancelarCita(SessionJwt.get(request), id);
		ra.addFlashAttribute("mensaje", "Cita cancelada.");
		return "redirect:/citas";
	}
}
