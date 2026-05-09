package com.duoc.backendS8.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import com.duoc.backendS8.dto.AnimalRequest;
import com.duoc.backendS8.dto.AnimalResponse;
import com.duoc.backendS8.entity.Animal;
import com.duoc.backendS8.entity.Dueno;
import com.duoc.backendS8.entity.EstadoAdopcion;
import com.duoc.backendS8.entity.GeneroAnimal;
import com.duoc.backendS8.repository.AnimalRepository;
import com.duoc.backendS8.repository.AnimalSpecifications;
import com.duoc.backendS8.repository.DuenoRepository;

@Service
public class AnimalService {

	private final AnimalRepository animalRepository;
	private final DuenoRepository duenoRepository;

	public AnimalService(AnimalRepository animalRepository, DuenoRepository duenoRepository) {
		this.animalRepository = animalRepository;
		this.duenoRepository = duenoRepository;
	}

	@Transactional(readOnly = true)
	public List<AnimalResponse> buscar(
			String especie,
			String raza,
			String ubicacion,
			Integer edadMin,
			Integer edadMax,
			GeneroAnimal genero,
			EstadoAdopcion estadoAdopcion) {
		Specification<Animal> spec = AnimalSpecifications.conFiltros(
				especie, raza, ubicacion, edadMin, edadMax, genero, estadoAdopcion);
		return animalRepository.findAll(spec, Sort.by("nombre")).stream().map(this::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public AnimalResponse obtenerPorId(Long id) {
		return animalRepository.findById(id)
				.map(this::toResponse)
				.orElseThrow(() -> new EntityNotFoundException("Animal no encontrado"));
	}

	@Transactional
	public AnimalResponse crear(AnimalRequest request) {
		Animal animal = new Animal();
		aplicar(animal, request);
		return toResponse(animalRepository.save(animal));
	}

	@Transactional
	public AnimalResponse actualizar(Long id, AnimalRequest request) {
		Animal animal = animalRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Animal no encontrado"));
		aplicar(animal, request);
		return toResponse(animalRepository.save(animal));
	}

	@Transactional
	public void eliminar(Long id) {
		if (!animalRepository.existsById(id)) {
			throw new EntityNotFoundException("Animal no encontrado");
		}
		animalRepository.deleteById(id);
	}

	private void aplicar(Animal animal, AnimalRequest request) {
		animal.setNombre(request.nombre().trim());
		animal.setEspecie(trim(request.especie()));
		animal.setRaza(trim(request.raza()));
		animal.setEdad(request.edad());
		animal.setUbicacion(trim(request.ubicacion()));
		animal.setGenero(request.genero());
		animal.setEstadoAdopcion(request.estadoAdopcion());
		animal.setFotoUrl(trim(request.fotoUrl()));
		if (request.duenoId() != null) {
			Dueno dueno = duenoRepository.findById(request.duenoId())
					.orElseThrow(() -> new EntityNotFoundException("Dueño no encontrado"));
			animal.setDueno(dueno);
		}
		else {
			animal.setDueno(null);
		}
	}

	private static String trim(String s) {
		return s != null && !s.isBlank() ? s.trim() : null;
	}

	private AnimalResponse toResponse(Animal animal) {
		Long duenoId = null;
		String duenoNombre = null;
		if (animal.getDueno() != null) {
			duenoId = animal.getDueno().getId();
			duenoNombre = animal.getDueno().getNombreCompleto();
		}
		return new AnimalResponse(
				animal.getId(),
				animal.getNombre(),
				animal.getEspecie(),
				animal.getRaza(),
				animal.getEdad(),
				animal.getUbicacion(),
				animal.getGenero(),
				animal.getEstadoAdopcion(),
				animal.getFotoUrl(),
				duenoId,
				duenoNombre);
	}
}
