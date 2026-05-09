package com.duoc.backendS8.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import com.duoc.backendS8.dto.DuenoRequest;
import com.duoc.backendS8.dto.DuenoResponse;
import com.duoc.backendS8.entity.Dueno;
import com.duoc.backendS8.repository.DuenoRepository;

@Service
public class DuenoService {

	private final DuenoRepository duenoRepository;

	public DuenoService(DuenoRepository duenoRepository) {
		this.duenoRepository = duenoRepository;
	}

	public List<DuenoResponse> listar() {
		return duenoRepository.findAll().stream().map(this::toResponse).toList();
	}

	public DuenoResponse obtener(Long id) {
		return duenoRepository.findById(id).map(this::toResponse)
				.orElseThrow(() -> new EntityNotFoundException("Dueño no encontrado"));
	}

	@Transactional
	public DuenoResponse crear(DuenoRequest request) {
		Dueno d = Dueno.builder()
				.nombreCompleto(request.nombreCompleto().trim())
				.email(trim(request.email()))
				.telefono(trim(request.telefono()))
				.direccion(trim(request.direccion()))
				.build();
		return toResponse(duenoRepository.save(d));
	}

	@Transactional
	public DuenoResponse actualizar(Long id, DuenoRequest request) {
		Dueno d = duenoRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Dueño no encontrado"));
		d.setNombreCompleto(request.nombreCompleto().trim());
		d.setEmail(trim(request.email()));
		d.setTelefono(trim(request.telefono()));
		d.setDireccion(trim(request.direccion()));
		return toResponse(duenoRepository.save(d));
	}

	@Transactional
	public void eliminar(Long id) {
		if (!duenoRepository.existsById(id)) {
			throw new EntityNotFoundException("Dueño no encontrado");
		}
		duenoRepository.deleteById(id);
	}

	private DuenoResponse toResponse(Dueno d) {
		return new DuenoResponse(d.getId(), d.getNombreCompleto(), d.getEmail(), d.getTelefono(), d.getDireccion());
	}

	private static String trim(String s) {
		return s != null && !s.isBlank() ? s.trim() : null;
	}
}
