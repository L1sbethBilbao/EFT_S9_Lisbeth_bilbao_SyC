package com.duoc.frontendS8.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.duoc.frontendS8.dto.AnimalDto;
import com.duoc.frontendS8.dto.AnimalForm;
import com.duoc.frontendS8.service.BackendApiClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/animales")
public class AnimalWebController {

	private final BackendApiClient backendApiClient;

	public AnimalWebController(BackendApiClient backendApiClient) {
		this.backendApiClient = backendApiClient;
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model, HttpServletRequest request) {
		model.addAttribute("animalForm", formVacio());
		model.addAttribute("modo", "crear");
		model.addAttribute("duenos", backendApiClient.listarDuenos(SessionJwt.get(request)));
		return "animal-form";
	}

	@PostMapping
	public String crear(
			@ModelAttribute("animalForm") @Valid AnimalForm animalForm,
			BindingResult bindingResult,
			Model model,
			HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("modo", "crear");
			model.addAttribute("duenos", backendApiClient.listarDuenos(SessionJwt.get(request)));
			return "animal-form";
		}
		backendApiClient.crearAnimal(SessionJwt.get(request), normalizar(animalForm));
		redirectAttributes.addFlashAttribute("mensaje", "Animal registrado correctamente.");
		return "redirect:/";
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model, HttpServletRequest request) {
		AnimalDto animal = backendApiClient.obtenerAnimal(id);
		AnimalForm form = new AnimalForm(
				animal.nombre(),
				s(animal.especie()),
				s(animal.raza()),
				animal.edad() != null ? animal.edad() : 0,
				s(animal.ubicacion()),
				s(animal.genero()),
				s(animal.estadoAdopcion()),
				s(animal.fotoUrl()),
				animal.duenoId());
		model.addAttribute("animalForm", form);
		model.addAttribute("animalId", id);
		model.addAttribute("modo", "editar");
		model.addAttribute("duenos", backendApiClient.listarDuenos(SessionJwt.get(request)));
		return "animal-form";
	}

	@PostMapping("/{id}/actualizar")
	public String actualizar(
			@PathVariable Long id,
			@ModelAttribute("animalForm") @Valid AnimalForm animalForm,
			BindingResult bindingResult,
			Model model,
			HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("animalId", id);
			model.addAttribute("modo", "editar");
			model.addAttribute("duenos", backendApiClient.listarDuenos(SessionJwt.get(request)));
			return "animal-form";
		}
		backendApiClient.actualizarAnimal(SessionJwt.get(request), id, normalizar(animalForm));
		redirectAttributes.addFlashAttribute("mensaje", "Animal actualizado.");
		return "redirect:/";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(
			@PathVariable Long id,
			HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		backendApiClient.eliminarAnimal(SessionJwt.get(request), id);
		redirectAttributes.addFlashAttribute("mensaje", "Animal eliminado.");
		return "redirect:/";
	}

	private static AnimalForm formVacio() {
		return new AnimalForm("", "", "", 0, "", "", "", "", null);
	}

	private static AnimalForm normalizar(AnimalForm form) {
		return new AnimalForm(
				form.nombre().trim(),
				blankToNull(form.especie()),
				blankToNull(form.raza()),
				form.edad(),
				blankToNull(form.ubicacion()),
				blankToNull(form.genero()),
				blankToNull(form.estadoAdopcion()),
				blankToNull(form.fotoUrl()),
				form.duenoId());
	}

	private static String blankToNull(String s) {
		return (s == null || s.isBlank()) ? null : s.trim();
	}

	private static String s(String v) {
		return v != null ? v : "";
	}
}
