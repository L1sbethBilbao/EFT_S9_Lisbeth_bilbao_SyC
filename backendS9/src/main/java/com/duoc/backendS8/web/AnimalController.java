package com.duoc.backendS8.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.backendS8.dto.AnimalRequest;
import com.duoc.backendS8.dto.AnimalResponse;
import com.duoc.backendS8.entity.EstadoAdopcion;
import com.duoc.backendS8.entity.GeneroAnimal;
import com.duoc.backendS8.service.AnimalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/animales")
public class AnimalController {

	private final AnimalService animalService;

	public AnimalController(AnimalService animalService) {
		this.animalService = animalService;
	}

	@GetMapping
	public List<AnimalResponse> listar(
			@RequestParam(required = false) String especie,
			@RequestParam(required = false) String raza,
			@RequestParam(required = false) String ubicacion,
			@RequestParam(required = false) Integer edadMin,
			@RequestParam(required = false) Integer edadMax,
			@RequestParam(required = false) GeneroAnimal genero,
			@RequestParam(required = false) EstadoAdopcion estadoAdopcion) {
		return animalService.buscar(especie, raza, ubicacion, edadMin, edadMax, genero, estadoAdopcion);
	}

	@GetMapping("/{id}")
	public AnimalResponse obtener(@PathVariable Long id) {
		return animalService.obtenerPorId(id);
	}

	@PostMapping
	public ResponseEntity<AnimalResponse> crear(@Valid @RequestBody AnimalRequest request) {
		return ResponseEntity.status(201).body(animalService.crear(request));
	}

	@PutMapping("/{id}")
	public AnimalResponse actualizar(@PathVariable Long id, @Valid @RequestBody AnimalRequest request) {
		return animalService.actualizar(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		animalService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
