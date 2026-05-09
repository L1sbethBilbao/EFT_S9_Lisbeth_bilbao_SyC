package com.duoc.backendS8.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import com.duoc.backendS8.dto.CitaRequest;
import com.duoc.backendS8.dto.CitaResponse;
import com.duoc.backendS8.entity.Animal;
import com.duoc.backendS8.entity.Cita;
import com.duoc.backendS8.entity.EstadoCita;
import com.duoc.backendS8.entity.RolUsuario;
import com.duoc.backendS8.entity.Usuario;
import com.duoc.backendS8.entity.Veterinario;
import com.duoc.backendS8.repository.AnimalRepository;
import com.duoc.backendS8.repository.CitaRepository;
import com.duoc.backendS8.repository.RegistroMedicoRepository;
import com.duoc.backendS8.repository.UsuarioRepository;
import com.duoc.backendS8.repository.VeterinarioRepository;

@Service
public class CitaService {

	private final CitaRepository citaRepository;
	private final AnimalRepository animalRepository;
	private final VeterinarioRepository veterinarioRepository;
	private final UsuarioRepository usuarioRepository;
	private final RegistroMedicoRepository registroMedicoRepository;

	public CitaService(
			CitaRepository citaRepository,
			AnimalRepository animalRepository,
			VeterinarioRepository veterinarioRepository,
			UsuarioRepository usuarioRepository,
			RegistroMedicoRepository registroMedicoRepository) {
		this.citaRepository = citaRepository;
		this.animalRepository = animalRepository;
		this.veterinarioRepository = veterinarioRepository;
		this.usuarioRepository = usuarioRepository;
		this.registroMedicoRepository = registroMedicoRepository;
	}

	@Transactional(readOnly = true)
	public List<CitaResponse> listar(Authentication auth) {
		Usuario u = usuarioRepository.findByUsernameFetchingVeterinario(auth.getName())
				.orElseThrow();
		List<Cita> citas;
		if (u.getRol() == RolUsuario.VETERINARIO) {
			if (u.getVeterinario() == null) {
				citas = List.of();
			}
			else {
				citas = citaRepository.findByVeterinario_IdOrderByFechaHoraDesc(u.getVeterinario().getId());
			}
		}
		else {
			citas = citaRepository.findAllByOrderByFechaHoraDesc();
		}
		return citas.stream().map(this::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public CitaResponse obtener(Long id, Authentication auth) {
		Cita cita = citaRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));
		assertPuedeAccederCita(auth, cita);
		return toResponse(cita);
	}

	@Transactional
	public CitaResponse crear(CitaRequest request, Authentication auth) {
		Usuario u = usuarioRepository.findByUsernameFetchingVeterinario(auth.getName())
				.orElseThrow();
		CitaRequest efectiva = request;
		if (u.getRol() == RolUsuario.VETERINARIO) {
			if (u.getVeterinario() == null) {
				throw new AccessDeniedException("Usuario veterinario sin perfil clínico asignado");
			}
			efectiva = new CitaRequest(
					request.animalId(),
					u.getVeterinario().getId(),
					request.fechaHora(),
					request.motivo());
		}
		Animal animal = animalRepository.findById(efectiva.animalId())
				.orElseThrow(() -> new EntityNotFoundException("Animal no encontrado"));
		Veterinario veterinario = veterinarioRepository.findById(efectiva.veterinarioId())
				.orElseThrow(() -> new EntityNotFoundException("Veterinario no encontrado"));
		if (!veterinario.isActivo()) {
			throw new IllegalStateException("El veterinario seleccionado no está activo");
		}
		Cita cita = Cita.builder()
				.animal(animal)
				.veterinario(veterinario)
				.fechaHora(efectiva.fechaHora())
				.motivo(trim(efectiva.motivo()))
				.estado(EstadoCita.PROGRAMADA)
				.build();
		return toResponse(citaRepository.save(cita));
	}

	@Transactional
	public void cancelar(Long id, Authentication auth) {
		Cita cita = citaRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));
		assertPuedeAccederCita(auth, cita);
		if (cita.getEstado() != EstadoCita.PROGRAMADA) {
			throw new IllegalStateException("Solo se pueden cancelar citas programadas");
		}
		cita.setEstado(EstadoCita.CANCELADA);
	}

	private void assertPuedeAccederCita(Authentication auth, Cita cita) {
		Usuario u = usuarioRepository.findByUsernameFetchingVeterinario(auth.getName())
				.orElseThrow();
		if (u.getRol() == RolUsuario.COORDINADOR) {
			return;
		}
		if (u.getRol() == RolUsuario.VETERINARIO && u.getVeterinario() != null
				&& cita.getVeterinario().getId().equals(u.getVeterinario().getId())) {
			return;
		}
		throw new AccessDeniedException("No puede operar sobre esta cita");
	}

	private CitaResponse toResponse(Cita c) {
		long n = registroMedicoRepository.countByCita_Animal_Id(c.getAnimal().getId());
		return new CitaResponse(
				c.getId(),
				c.getAnimal().getId(),
				c.getAnimal().getNombre(),
				c.getVeterinario().getId(),
				c.getVeterinario().getNombre(),
				c.getFechaHora(),
				c.getMotivo(),
				c.getEstado(),
				n > 0);
	}

	private static String trim(String s) {
		return s != null && !s.isBlank() ? s.trim() : null;
	}
}
