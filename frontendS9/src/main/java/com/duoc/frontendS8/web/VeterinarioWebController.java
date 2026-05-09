package com.duoc.frontendS8.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.duoc.frontendS8.dto.VeterinarioForm;
import com.duoc.frontendS8.service.BackendApiClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/veterinarios")
public class VeterinarioWebController {

	private final BackendApiClient backendApiClient;

	public VeterinarioWebController(BackendApiClient backendApiClient) {
		this.backendApiClient = backendApiClient;
	}

	@GetMapping
	public String listar(Model model, HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		model.addAttribute("veterinarios", backendApiClient.listarVeterinarios(SessionJwt.get(request)));
		return "veterinarios";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("vetForm", new VeterinarioForm("", "", true));
		model.addAttribute("modo", "crear");
		return "veterinario-form";
	}

	@PostMapping
	public String crear(
			@Valid @ModelAttribute("vetForm") VeterinarioForm form,
			BindingResult bindingResult,
			Model model,
			HttpServletRequest request,
			RedirectAttributes ra) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("modo", "crear");
			return "veterinario-form";
		}
		backendApiClient.crearVeterinario(SessionJwt.get(request), form);
		ra.addFlashAttribute("mensaje", "Veterinario registrado.");
		ra.addFlashAttribute("mostrarIrCitas", true);
		return "redirect:/veterinarios";
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model, HttpServletRequest request) {
		var v = backendApiClient.listarVeterinarios(SessionJwt.get(request)).stream()
				.filter(x -> x.id().equals(id))
				.findFirst()
				.orElseThrow();
		model.addAttribute("vetForm", new VeterinarioForm(v.nombre(), v.especialidad(), v.activo()));
		model.addAttribute("vetId", id);
		model.addAttribute("modo", "editar");
		return "veterinario-form";
	}

	@PostMapping("/{id}/actualizar")
	public String actualizar(
			@PathVariable Long id,
			@Valid @ModelAttribute("vetForm") VeterinarioForm form,
			BindingResult bindingResult,
			Model model,
			HttpServletRequest request,
			RedirectAttributes ra) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("vetId", id);
			model.addAttribute("modo", "editar");
			return "veterinario-form";
		}
		backendApiClient.actualizarVeterinario(SessionJwt.get(request), id, form);
		ra.addFlashAttribute("mensaje", "Veterinario actualizado.");
		return "redirect:/veterinarios";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable Long id, HttpServletRequest request, RedirectAttributes ra) {
		backendApiClient.eliminarVeterinario(SessionJwt.get(request), id);
		ra.addFlashAttribute("mensaje", "Veterinario eliminado.");
		return "redirect:/veterinarios";
	}
}
