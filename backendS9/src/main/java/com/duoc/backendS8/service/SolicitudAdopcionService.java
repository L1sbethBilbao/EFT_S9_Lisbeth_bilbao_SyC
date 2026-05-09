package com.duoc.backendS8.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duoc.backendS8.dto.EstadoSolicitudPatchRequest;
import com.duoc.backendS8.dto.MensajeCoordinadorRequest;
import com.duoc.backendS8.dto.MensajeSeguimientoRequest;
import com.duoc.backendS8.dto.MensajeSolicitudResponse;
import com.duoc.backendS8.dto.SolicitudAdopcionCreateRequest;
import com.duoc.backendS8.dto.SolicitudAdopcionCreatedResponse;
import com.duoc.backendS8.dto.SolicitudAdopcionDatosPatchRequest;
import com.duoc.backendS8.dto.MensajeSeguimientoPublicoResponse;
import com.duoc.backendS8.dto.SolicitudAdopcionDetalleResponse;
import com.duoc.backendS8.dto.SolicitudAdopcionListItemResponse;
import com.duoc.backendS8.dto.SolicitudAdopcionSeguimientoPublicoResponse;
import com.duoc.backendS8.entity.Animal;
import com.duoc.backendS8.entity.Dueno;
import com.duoc.backendS8.entity.EstadoAdopcion;
import com.duoc.backendS8.entity.EstadoSolicitudAdopcion;
import com.duoc.backendS8.entity.MensajeSolicitudAdopcion;
import com.duoc.backendS8.entity.RolMensajeSolicitudAdopcion;
import com.duoc.backendS8.entity.SolicitudAdopcion;
import com.duoc.backendS8.repository.AnimalRepository;
import com.duoc.backendS8.repository.DuenoRepository;
import com.duoc.backendS8.repository.MensajeSolicitudAdopcionRepository;
import com.duoc.backendS8.repository.SolicitudAdopcionRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SolicitudAdopcionService {

	private final SolicitudAdopcionRepository solicitudRepository;
	private final MensajeSolicitudAdopcionRepository mensajeRepository;
	private final AnimalRepository animalRepository;
	private final DuenoRepository duenoRepository;

	public SolicitudAdopcionService(
			SolicitudAdopcionRepository solicitudRepository,
			MensajeSolicitudAdopcionRepository mensajeRepository,
			AnimalRepository animalRepository,
			DuenoRepository duenoRepository) {
		this.solicitudRepository = solicitudRepository;
		this.mensajeRepository = mensajeRepository;
		this.animalRepository = animalRepository;
		this.duenoRepository = duenoRepository;
	}

	@Transactional
	public SolicitudAdopcionCreatedResponse crear(SolicitudAdopcionCreateRequest req) {
		Animal animal = animalRepository.findById(req.animalId())
				.orElseThrow(() -> new EntityNotFoundException("Animal no encontrado"));
		EstadoAdopcion ea = animal.getEstadoAdopcion();
		if (ea != EstadoAdopcion.DISPONIBLE && ea != EstadoAdopcion.EN_ACOGIDA) {
			throw new IllegalStateException(
					"Este animal no acepta solicitudes de adopción en su estado actual (" + ea + ").");
		}
		String email = req.email().trim();
		if (solicitudRepository.existsByAnimal_IdAndEmailIgnoreCaseAndEstadoIn(
				animal.getId(),
				email,
				List.of(
						EstadoSolicitudAdopcion.PENDIENTE,
						EstadoSolicitudAdopcion.EN_REVISION,
						EstadoSolicitudAdopcion.APROBADA))) {
			throw new IllegalStateException(
					"Ya existe una solicitud de adopción con este correo para la misma mascota. "
							+ "Ingrese a Seguimiento con el código que recibió para ver el estado o escribir a coordinación. "
							+ "Si su solicitud anterior fue rechazada, podrá volver a postular.");
		}
		SolicitudAdopcion s = SolicitudAdopcion.builder()
				.codigoSeguimiento(UUID.randomUUID().toString())
				.animal(animal)
				.nombreCompleto(req.nombreCompleto().trim())
				.email(email)
				.telefono(trimToNull(req.telefono()))
				.direccion(trimToNull(req.direccion()))
				.ciudad(trimToNull(req.ciudad()))
				.tipoVivienda(req.tipoVivienda())
				.personasEnHogar(req.personasEnHogar())
				.tieneNinos(Boolean.TRUE.equals(req.tieneNinos()))
				.tieneOtrasMascotas(Boolean.TRUE.equals(req.tieneOtrasMascotas()))
				.experienciaMascotas(trimToNull(req.experienciaMascotas()))
				.motivacionAdopcion(req.motivacionAdopcion().trim())
				.estado(EstadoSolicitudAdopcion.PENDIENTE)
				.build();
		solicitudRepository.save(s);
		MensajeSolicitudAdopcion primer = MensajeSolicitudAdopcion.builder()
				.solicitud(s)
				.rolAutor(RolMensajeSolicitudAdopcion.SOLICITANTE)
				.cuerpo("Motivación declarada en el formulario:\n\n" + s.getMotivacionAdopcion())
				.build();
		mensajeRepository.save(primer);
		return new SolicitudAdopcionCreatedResponse(
				s.getId(),
				s.getCodigoSeguimiento(),
				animal.getNombre(),
				"Guarde su código de seguimiento para enviar mensajes a coordinación.");
	}

	@Transactional
	public void agregarMensajeSolicitante(MensajeSeguimientoRequest req) {
		SolicitudAdopcion s = solicitudRepository.findByCodigoSeguimiento(req.codigoSeguimiento().trim())
				.orElseThrow(() -> new EntityNotFoundException("Código de seguimiento no válido"));
		if (s.getEstado() == EstadoSolicitudAdopcion.RECHAZADA || s.getEstado() == EstadoSolicitudAdopcion.APROBADA) {
			throw new IllegalStateException("Esta solicitud está cerrada; no se pueden añadir mensajes.");
		}
		MensajeSolicitudAdopcion m = MensajeSolicitudAdopcion.builder()
				.solicitud(s)
				.rolAutor(RolMensajeSolicitudAdopcion.SOLICITANTE)
				.cuerpo(req.cuerpo().trim())
				.build();
		mensajeRepository.save(m);
	}

	@Transactional(readOnly = true)
	public List<SolicitudAdopcionListItemResponse> listarParaCoordinador() {
		return solicitudRepository.findAllByOrderByCreatedAtDesc().stream()
				.map(s -> new SolicitudAdopcionListItemResponse(
						s.getId(),
						s.getCodigoSeguimiento(),
						s.getAnimal().getId(),
						s.getAnimal().getNombre(),
						s.getNombreCompleto(),
						s.getEmail(),
						s.getEstado(),
						s.getCreatedAt()))
				.toList();
	}

	@Transactional(readOnly = true)
	public SolicitudAdopcionDetalleResponse obtenerParaCoordinador(Long id) {
		SolicitudAdopcion s = solicitudRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));
		List<MensajeSolicitudResponse> mensajes = mensajeRepository.findBySolicitudIdOrderByCreatedAtAsc(id).stream()
				.map(m -> new MensajeSolicitudResponse(m.getId(), m.getRolAutor(), m.getCuerpo(), m.getCreatedAt()))
				.toList();
		Animal a = s.getAnimal();
		return new SolicitudAdopcionDetalleResponse(
				s.getId(),
				s.getCodigoSeguimiento(),
				a.getId(),
				a.getNombre(),
				s.getNombreCompleto(),
				s.getEmail(),
				s.getTelefono(),
				s.getDireccion(),
				s.getCiudad(),
				s.getTipoVivienda(),
				s.getPersonasEnHogar(),
				s.getTieneNinos(),
				s.getTieneOtrasMascotas(),
				s.getExperienciaMascotas(),
				s.getMotivacionAdopcion(),
				s.getEstado(),
				s.getCreatedAt(),
				mensajes);
	}

	@Transactional
	public void agregarMensajeCoordinador(Long id, MensajeCoordinadorRequest req) {
		SolicitudAdopcion s = solicitudRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));
		MensajeSolicitudAdopcion m = MensajeSolicitudAdopcion.builder()
				.solicitud(s)
				.rolAutor(RolMensajeSolicitudAdopcion.COORDINADOR)
				.cuerpo(req.cuerpo().trim())
				.build();
		mensajeRepository.save(m);
		if (s.getEstado() == EstadoSolicitudAdopcion.PENDIENTE) {
			s.setEstado(EstadoSolicitudAdopcion.EN_REVISION);
		}
	}

	@Transactional
	public void actualizarEstado(Long id, EstadoSolicitudPatchRequest req) {
		SolicitudAdopcion s = solicitudRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));
		EstadoSolicitudAdopcion anterior = s.getEstado();
		EstadoSolicitudAdopcion nuevo = req.estado();
		s.setEstado(nuevo);
		solicitudRepository.save(s);
		if (nuevo == EstadoSolicitudAdopcion.APROBADA) {
			Animal animal = animalRepository.findById(s.getAnimal().getId()).orElseThrow();
			boolean debeVincular = anterior != EstadoSolicitudAdopcion.APROBADA || animal.getDueno() == null;
			if (debeVincular) {
				registrarAdopcionEnDueñoYMascota(s);
			}
		}
	}

	/**
	 * Repara solicitudes ya marcadas como aprobadas cuyo animal aún no tiene dueño
	 * (p. ej. datos creados antes de esta lógica). Idempotente.
	 */
	@Transactional
	public void reconciliarAprobadasSinDueñoEnAnimal() {
		for (SolicitudAdopcion s : solicitudRepository
				.findAprobadasConAnimalSinDueno(EstadoSolicitudAdopcion.APROBADA)) {
			registrarAdopcionEnDueñoYMascota(s);
		}
	}

	private void registrarAdopcionEnDueñoYMascota(SolicitudAdopcion s) {
		Long animalId = s.getAnimal().getId();
		Animal animal = animalRepository.findById(animalId)
				.orElseThrow(() -> new EntityNotFoundException("Animal no encontrado"));
		String email = s.getEmail().trim();
		Dueno dueno = duenoRepository.findByEmailIgnoreCase(email)
				.orElseGet(() -> Dueno.builder()
						.nombreCompleto(s.getNombreCompleto().trim())
						.email(email)
						.telefono(trimToNull(s.getTelefono()))
						.direccion(construirDireccionDueno(s))
						.build());
		dueno.setNombreCompleto(s.getNombreCompleto().trim());
		dueno.setTelefono(trimToNull(s.getTelefono()));
		dueno.setDireccion(construirDireccionDueno(s));
		if (dueno.getEmail() == null || dueno.getEmail().isBlank()) {
			dueno.setEmail(email);
		}
		dueno = duenoRepository.save(dueno);
		animal.setDueno(dueno);
		animal.setEstadoAdopcion(EstadoAdopcion.ADOPTADO);
		animalRepository.save(animal);
	}

	private static String construirDireccionDueno(SolicitudAdopcion s) {
		String d = trimToNull(s.getDireccion());
		String c = trimToNull(s.getCiudad());
		if (d == null && c == null) {
			return null;
		}
		if (d == null) {
			return c;
		}
		if (c == null) {
			return d;
		}
		return d + ", " + c;
	}

	@Transactional
	public void actualizarDatosCoordinador(Long id, SolicitudAdopcionDatosPatchRequest req) {
		SolicitudAdopcion s = solicitudRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));
		if (s.getEstado() == EstadoSolicitudAdopcion.APROBADA || s.getEstado() == EstadoSolicitudAdopcion.RECHAZADA) {
			throw new IllegalStateException(
					"No se pueden corregir datos de una solicitud cerrada (aprobada o rechazada).");
		}
		String newEmail = req.email().trim();
		Long animalId = s.getAnimal().getId();
		if (!newEmail.equalsIgnoreCase(s.getEmail().trim())) {
			if (solicitudRepository.existsByAnimal_IdAndEmailIgnoreCaseAndEstadoInAndIdNot(
					animalId,
					newEmail,
					List.of(
							EstadoSolicitudAdopcion.PENDIENTE,
							EstadoSolicitudAdopcion.EN_REVISION,
							EstadoSolicitudAdopcion.APROBADA),
					id)) {
				throw new IllegalStateException(
						"Ya existe otra solicitud activa con ese correo electrónico para la misma mascota.");
			}
		}
		s.setNombreCompleto(req.nombreCompleto().trim());
		s.setEmail(newEmail);
		s.setTelefono(trimToNull(req.telefono()));
		s.setDireccion(trimToNull(req.direccion()));
		s.setCiudad(trimToNull(req.ciudad()));
		s.setTipoVivienda(req.tipoVivienda());
		s.setPersonasEnHogar(req.personasEnHogar());
		s.setTieneNinos(Boolean.TRUE.equals(req.tieneNinos()));
		s.setTieneOtrasMascotas(Boolean.TRUE.equals(req.tieneOtrasMascotas()));
		s.setExperienciaMascotas(trimToNull(req.experienciaMascotas()));
		s.setMotivacionAdopcion(req.motivacionAdopcion().trim());
		if (s.getEstado() == EstadoSolicitudAdopcion.PENDIENTE) {
			s.setEstado(EstadoSolicitudAdopcion.EN_REVISION);
		}
		solicitudRepository.save(s);
		String constancia = req.constanciaSolicitante().trim();
		MensajeSolicitudAdopcion logCambio = MensajeSolicitudAdopcion.builder()
				.solicitud(s)
				.rolAutor(RolMensajeSolicitudAdopcion.COORDINADOR)
				.cuerpo("Actualización de datos del formulario a petición del solicitante.\n\n"
						+ "Constancia registrada por coordinación:\n"
						+ constancia)
				.build();
		mensajeRepository.save(logCambio);
	}

	@Transactional
	public void eliminar(Long id) {
		if (!solicitudRepository.existsById(id)) {
			throw new EntityNotFoundException("Solicitud no encontrada");
		}
		mensajeRepository.deleteBySolicitud_Id(id);
		solicitudRepository.deleteById(id);
	}

	@Transactional(readOnly = true)
	public SolicitudAdopcionSeguimientoPublicoResponse obtenerVistaPublicaPorCodigo(String codigo) {
		if (codigo == null || codigo.isBlank()) {
			throw new EntityNotFoundException("Código de seguimiento no válido");
		}
		SolicitudAdopcion s = solicitudRepository.findByCodigoSeguimiento(codigo.trim())
				.orElseThrow(() -> new EntityNotFoundException("Código de seguimiento no encontrado"));
		List<MensajeSeguimientoPublicoResponse> mensajes = mensajeRepository.findBySolicitudIdOrderByCreatedAtAsc(s.getId()).stream()
				.map(m -> new MensajeSeguimientoPublicoResponse(m.getRolAutor(), m.getCuerpo(), m.getCreatedAt()))
				.toList();
		Animal a = s.getAnimal();
		return new SolicitudAdopcionSeguimientoPublicoResponse(
				s.getCodigoSeguimiento(),
				s.getEstado(),
				descripcionEstadoPublico(s.getEstado()),
				a.getNombre(),
				mensajes);
	}

	private static String descripcionEstadoPublico(EstadoSolicitudAdopcion e) {
		if (e == null) {
			return "";
		}
		return switch (e) {
			case PENDIENTE -> "Su solicitud está registrada y pendiente de revisión por coordinación.";
			case EN_REVISION -> "Un coordinador está revisando su solicitud. Puede leer mensajes abajo.";
			case APROBADA -> "Su solicitud fue aprobada en el sistema. Coordinación puede contactarla por los datos que entregó.";
			case RECHAZADA -> "Esta solicitud fue cerrada sin aprobación. Si tiene dudas, puede escribirnos indicando su código.";
		};
	}

	private static String trimToNull(String s) {
		if (s == null) {
			return null;
		}
		String t = s.trim();
		return t.isEmpty() ? null : t;
	}
}
