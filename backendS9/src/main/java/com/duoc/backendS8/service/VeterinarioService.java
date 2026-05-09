package com.duoc.backendS8.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import com.duoc.backendS8.dto.VeterinarioRequest;
import com.duoc.backendS8.dto.VeterinarioResponse;
import com.duoc.backendS8.entity.Veterinario;
import com.duoc.backendS8.repository.VeterinarioRepository;

@Service
public class VeterinarioService {

	private final VeterinarioRepository veterinarioRepository;

	public VeterinarioService(VeterinarioRepository veterinarioRepository) {
		this.veterinarioRepository = veterinarioRepository;
	}

	public List<VeterinarioResponse> listarActivos() {
		return veterinarioRepository.findAll().stream()
				.filter(Veterinario::isActivo)
				.sorted(Comparator.comparing(Veterinario::getNombre, Comparator.nullsLast(String::compareToIgnoreCase)))
				.map(this::toResponse)
				.toList();
	}

	public List<VeterinarioResponse> listarTodos() {
		return veterinarioRepository.findAll().stream().map(this::toResponse).toList();
	}

	public VeterinarioResponse obtener(Long id) {
		return veterinarioRepository.findById(id).map(this::toResponse)
				.orElseThrow(() -> new EntityNotFoundException("Veterinario no encontrado"));
	}

	@Transactional
	public VeterinarioResponse crear(VeterinarioRequest request) {
		Veterinario v = Veterinario.builder()
				.nombre(request.nombre().trim())
				.especialidad(trim(request.especialidad()))
				.activo(request.activo() == null || request.activo())
				.build();
		return toResponse(veterinarioRepository.save(v));
	}

	@Transactional
	public VeterinarioResponse actualizar(Long id, VeterinarioRequest request) {
		Veterinario v = veterinarioRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Veterinario no encontrado"));
		v.setNombre(request.nombre().trim());
		v.setEspecialidad(trim(request.especialidad()));
		if (request.activo() != null) {
			v.setActivo(request.activo());
		}
		return toResponse(veterinarioRepository.save(v));
	}

	@Transactional
	public void eliminar(Long id) {
		if (!veterinarioRepository.existsById(id)) {
			throw new EntityNotFoundException("Veterinario no encontrado");
		}
		veterinarioRepository.deleteById(id);
	}

	private VeterinarioResponse toResponse(Veterinario v) {
		return new VeterinarioResponse(v.getId(), v.getNombre(), v.getEspecialidad(), v.isActivo());
	}

	private static String trim(String s) {
		return s != null && !s.isBlank() ? s.trim() : null;
	}
}
