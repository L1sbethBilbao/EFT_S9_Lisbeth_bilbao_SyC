package com.duoc.backendS8.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import com.duoc.backendS8.dto.RegistroMedicoRequest;
import com.duoc.backendS8.dto.RegistroMedicoResponse;
import com.duoc.backendS8.entity.Cita;
import com.duoc.backendS8.entity.EstadoCita;
import com.duoc.backendS8.entity.RegistroMedico;
import com.duoc.backendS8.repository.CitaRepository;
import com.duoc.backendS8.repository.RegistroMedicoRepository;

@Service
public class RegistroMedicoService {

	private final RegistroMedicoRepository registroMedicoRepository;
	private final CitaRepository citaRepository;

	public RegistroMedicoService(
			RegistroMedicoRepository registroMedicoRepository,
			CitaRepository citaRepository) {
		this.registroMedicoRepository = registroMedicoRepository;
		this.citaRepository = citaRepository;
	}

	@Transactional(readOnly = true)
	public List<RegistroMedicoResponse> listar() {
		return registroMedicoRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public RegistroMedicoResponse obtener(Long id) {
		return registroMedicoRepository.findById(id).map(this::toResponse)
				.orElseThrow(() -> new EntityNotFoundException("Registro médico no encontrado"));
	}

	@Transactional
	public RegistroMedicoResponse crear(RegistroMedicoRequest request) {
		Cita cita = citaRepository.findById(request.citaId())
				.orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));
		if (cita.getEstado() != EstadoCita.PROGRAMADA) {
			throw new IllegalStateException("Solo se puede registrar atención en citas en estado PROGRAMADA");
		}
		if (registroMedicoRepository.findByCita_Id(cita.getId()).isPresent()) {
			throw new IllegalStateException("Ya existe una ficha para esta cita");
		}
		RegistroMedico rm = RegistroMedico.builder()
				.cita(cita)
				.fechaAtencion(request.fechaAtencion())
				.diagnostico(request.diagnostico().trim())
				.tratamiento(trim(request.tratamiento()))
				.medicamentos(trim(request.medicamentos()))
				.notas(trim(request.notas()))
				.build();
		cita.setEstado(EstadoCita.COMPLETADA);
		RegistroMedico guardado = registroMedicoRepository.save(rm);
		return toResponse(guardado);
	}

	private RegistroMedicoResponse toResponse(RegistroMedico rm) {
		Long facturaId = rm.getFactura() != null ? rm.getFactura().getId() : null;
		return new RegistroMedicoResponse(
				rm.getId(),
				rm.getCita().getId(),
				rm.getFechaAtencion(),
				rm.getDiagnostico(),
				rm.getTratamiento(),
				rm.getMedicamentos(),
				rm.getNotas(),
				facturaId);
	}

	private static String trim(String s) {
		return s != null && !s.isBlank() ? s.trim() : null;
	}
}
