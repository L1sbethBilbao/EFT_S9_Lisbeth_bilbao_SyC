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

import com.duoc.frontendS8.dto.DuenoForm;
import com.duoc.frontendS8.service.BackendApiClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/duenos")
public class DuenoWebController {

	private final BackendApiClient backendApiClient;

	public DuenoWebController(BackendApiClient backendApiClient) {
		this.backendApiClient = backendApiClient;
	}

	@GetMapping
	public String listar(Model model, HttpServletRequest request) {
		model.addAttribute("duenos", backendApiClient.listarDuenos(SessionJwt.get(request)));
		return "duenos";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("duenoForm", new DuenoForm("", "", "", ""));
		model.addAttribute("modo", "crear");
		return "dueno-form";
	}

	@PostMapping
	public String crear(
			@Valid @ModelAttribute("duenoForm") DuenoForm form,
			BindingResult bindingResult,
			Model model,
			HttpServletRequest request,
			RedirectAttributes ra) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("modo", "crear");
			return "dueno-form";
		}
		backendApiClient.crearDueno(SessionJwt.get(request), form);
		ra.addFlashAttribute("mensaje", "Dueño registrado.");
		return "redirect:/duenos";
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model, HttpServletRequest request) {
		var d = backendApiClient.listarDuenos(SessionJwt.get(request)).stream()
				.filter(x -> x.id().equals(id))
				.findFirst()
				.orElseThrow();
		model.addAttribute("duenoForm", new DuenoForm(d.nombreCompleto(), d.email(), d.telefono(), d.direccion()));
		model.addAttribute("duenoId", id);
		model.addAttribute("modo", "editar");
		return "dueno-form";
	}

	@PostMapping("/{id}/actualizar")
	public String actualizar(
			@PathVariable Long id,
			@Valid @ModelAttribute("duenoForm") DuenoForm form,
			BindingResult bindingResult,
			Model model,
			HttpServletRequest request,
			RedirectAttributes ra) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("duenoId", id);
			model.addAttribute("modo", "editar");
			return "dueno-form";
		}
		backendApiClient.actualizarDueno(SessionJwt.get(request), id, form);
		ra.addFlashAttribute("mensaje", "Dueño actualizado.");
		return "redirect:/duenos";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable Long id, HttpServletRequest request, RedirectAttributes ra) {
		backendApiClient.eliminarDueno(SessionJwt.get(request), id);
		ra.addFlashAttribute("mensaje", "Dueño eliminado.");
		return "redirect:/duenos";
	}
}
