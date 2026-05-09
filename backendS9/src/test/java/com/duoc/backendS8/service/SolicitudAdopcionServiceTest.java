package com.duoc.backendS8.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.duoc.backendS8.dto.EstadoSolicitudPatchRequest;
import com.duoc.backendS8.dto.MensajeCoordinadorRequest;
import com.duoc.backendS8.dto.MensajeSeguimientoRequest;
import com.duoc.backendS8.dto.SolicitudAdopcionCreateRequest;
import com.duoc.backendS8.dto.SolicitudAdopcionDatosPatchRequest;
import com.duoc.backendS8.entity.Animal;
import com.duoc.backendS8.entity.Dueno;
import com.duoc.backendS8.entity.EstadoAdopcion;
import com.duoc.backendS8.entity.EstadoSolicitudAdopcion;
import com.duoc.backendS8.entity.MensajeSolicitudAdopcion;
import com.duoc.backendS8.entity.RolMensajeSolicitudAdopcion;
import com.duoc.backendS8.entity.SolicitudAdopcion;
import com.duoc.backendS8.entity.TipoViviendaAdopcion;
import com.duoc.backendS8.repository.AnimalRepository;
import com.duoc.backendS8.repository.DuenoRepository;
import com.duoc.backendS8.repository.MensajeSolicitudAdopcionRepository;
import com.duoc.backendS8.repository.SolicitudAdopcionRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class SolicitudAdopcionServiceTest {

	@Mock
	private SolicitudAdopcionRepository solicitudRepository;
	@Mock
	private MensajeSolicitudAdopcionRepository mensajeRepository;
	@Mock
	private AnimalRepository animalRepository;
	@Mock
	private DuenoRepository duenoRepository;

	@InjectMocks
	private SolicitudAdopcionService service;

	@Test
	void crear_rechazaDuplicadoMismoCorreoYMismaMascota() {
		Animal alf = Animal.builder().id(10L).nombre("Alf").estadoAdopcion(EstadoAdopcion.DISPONIBLE).build();
		when(animalRepository.findById(10L)).thenReturn(Optional.of(alf));
		when(solicitudRepository.existsByAnimal_IdAndEmailIgnoreCaseAndEstadoIn(eq(10L), eq("a@b.cl"), anyList()))
				.thenReturn(true);

		var req = new SolicitudAdopcionCreateRequest(
				10L,
				"Nombre",
				"a@b.cl",
				null,
				null,
				null,
				TipoViviendaAdopcion.CASA,
				2,
				false,
				false,
				null,
				"motivo");

		assertThatThrownBy(() -> service.crear(req))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("Ya existe una solicitud");

		verify(solicitudRepository, never()).save(any());
	}

	@Test
	void eliminar_borraMensajesYFila() {
		when(solicitudRepository.existsById(5L)).thenReturn(true);
		service.eliminar(5L);
		verify(mensajeRepository).deleteBySolicitud_Id(5L);
		verify(solicitudRepository).deleteById(5L);
	}

	@Test
	void eliminar_idInexistente_lanza() {
		when(solicitudRepository.existsById(99L)).thenReturn(false);
		assertThatThrownBy(() -> service.eliminar(99L))
				.isInstanceOf(EntityNotFoundException.class);
		verify(mensajeRepository, never()).deleteBySolicitud_Id(anyLong());
		verify(solicitudRepository, never()).deleteById(anyLong());
	}

	@Test
	void actualizarDatosCoordinador_solicitudRechazada_noPermite() {
		Animal a = Animal.builder().id(9L).nombre("Luna").estadoAdopcion(EstadoAdopcion.DISPONIBLE).build();
		SolicitudAdopcion s = SolicitudAdopcion.builder()
				.id(1L)
				.codigoSeguimiento("c1")
				.animal(a)
				.nombreCompleto("N")
				.email("e@test.cl")
				.tipoVivienda(TipoViviendaAdopcion.CASA)
				.personasEnHogar(1)
				.motivacionAdopcion("mot")
				.estado(EstadoSolicitudAdopcion.RECHAZADA)
				.build();
		when(solicitudRepository.findById(1L)).thenReturn(Optional.of(s));
		var req = new SolicitudAdopcionDatosPatchRequest(
				"Otro nombre",
				"e@test.cl",
				null,
				null,
				null,
				TipoViviendaAdopcion.DEPARTAMENTO,
				2,
				false,
				false,
				null,
				"nueva motivación suficientemente larga",
				"El solicitante pidió corrección por seguimiento el 07/05/2026.");
		assertThatThrownBy(() -> service.actualizarDatosCoordinador(1L, req))
				.isInstanceOf(IllegalStateException.class);
		verify(mensajeRepository, never()).save(any());
		verify(solicitudRepository, never()).save(any());
	}

	@Test
	void actualizarEstado_aprobada_registraDueñoYMarcaAnimalAdoptado() {
		Animal alf = Animal.builder().id(10L).nombre("Alf").estadoAdopcion(EstadoAdopcion.DISPONIBLE).build();
		SolicitudAdopcion s = SolicitudAdopcion.builder()
				.id(2L)
				.codigoSeguimiento("c1")
				.animal(alf)
				.nombreCompleto("Lisbeth Bilbao")
				.email("lisbeth@test.cl")
				.telefono("+56911112222")
				.estado(EstadoSolicitudAdopcion.EN_REVISION)
				.build();
		when(solicitudRepository.findById(2L)).thenReturn(Optional.of(s));
		when(animalRepository.findById(10L)).thenReturn(Optional.of(alf));
		when(duenoRepository.findByEmailIgnoreCase("lisbeth@test.cl")).thenReturn(Optional.empty());
		when(duenoRepository.save(any(Dueno.class))).thenAnswer(inv -> {
			Dueno d = inv.getArgument(0);
			d.setId(99L);
			return d;
		});

		service.actualizarEstado(2L, new EstadoSolicitudPatchRequest(EstadoSolicitudAdopcion.APROBADA));

		assertThat(s.getEstado()).isEqualTo(EstadoSolicitudAdopcion.APROBADA);
		verify(duenoRepository).save(any(Dueno.class));
		verify(animalRepository).save(argThat(a ->
				a.getEstadoAdopcion() == EstadoAdopcion.ADOPTADO && a.getDueno() != null));
		verify(solicitudRepository).save(s);
	}

	@Test
	void actualizarEstado_aprobada_yaVinculada_noRepitePersistenciaDueno() {
		Dueno d = Dueno.builder().id(1L).email("x@y.cl").nombreCompleto("X").build();
		Animal alf = Animal.builder().id(10L).nombre("Alf").estadoAdopcion(EstadoAdopcion.ADOPTADO).dueno(d).build();
		SolicitudAdopcion s = SolicitudAdopcion.builder()
				.id(2L)
				.animal(alf)
				.nombreCompleto("X")
				.email("x@y.cl")
				.estado(EstadoSolicitudAdopcion.APROBADA)
				.build();
		when(solicitudRepository.findById(2L)).thenReturn(Optional.of(s));
		when(animalRepository.findById(10L)).thenReturn(Optional.of(alf));

		service.actualizarEstado(2L, new EstadoSolicitudPatchRequest(EstadoSolicitudAdopcion.APROBADA));

		verify(duenoRepository, never()).save(any());
		verify(animalRepository, never()).save(any());
	}

	@Test
	void reconciliarAprobadasSinDueñoEnAnimal_vinculaMascotaSinDueno() {
		Animal alf = Animal.builder().id(10L).nombre("Alf").estadoAdopcion(EstadoAdopcion.DISPONIBLE).build();
		SolicitudAdopcion s = SolicitudAdopcion.builder()
				.id(3L)
				.codigoSeguimiento("cx")
				.animal(alf)
				.nombreCompleto("María")
				.email("maria@test.cl")
				.estado(EstadoSolicitudAdopcion.APROBADA)
				.build();
		when(solicitudRepository.findAprobadasConAnimalSinDueno(EstadoSolicitudAdopcion.APROBADA))
				.thenReturn(List.of(s));
		when(animalRepository.findById(10L)).thenReturn(Optional.of(alf));
		when(duenoRepository.findByEmailIgnoreCase("maria@test.cl")).thenReturn(Optional.empty());
		when(duenoRepository.save(any(Dueno.class))).thenAnswer(inv -> {
			Dueno d = inv.getArgument(0);
			d.setId(5L);
			return d;
		});

		service.reconciliarAprobadasSinDueñoEnAnimal();

		verify(duenoRepository).save(any(Dueno.class));
		verify(animalRepository).save(any(Animal.class));
	}

	@Test
	void crear_animalNoEncontrado_lanza() {
		when(animalRepository.findById(1L)).thenReturn(Optional.empty());
		var req = new SolicitudAdopcionCreateRequest(
				1L, "N", "a@b.cl", null, null, null, TipoViviendaAdopcion.CASA, 1,
				false, false, null, "motivo");
		assertThatThrownBy(() -> service.crear(req)).isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void crear_estadoAnimalNoPermite_lanza() {
		Animal a = Animal.builder().id(1L).nombre("K").estadoAdopcion(EstadoAdopcion.ADOPTADO).build();
		when(animalRepository.findById(1L)).thenReturn(Optional.of(a));
		var req = new SolicitudAdopcionCreateRequest(
				1L, "N", "a@b.cl", null, null, null, TipoViviendaAdopcion.CASA, 1,
				false, false, null, "motivo");
		assertThatThrownBy(() -> service.crear(req)).isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("no acepta solicitudes");
	}

	@Test
	void crear_ok_disponible_persisteYSerializaRespuesta() {
		Animal alf = Animal.builder().id(10L).nombre("Alf").estadoAdopcion(EstadoAdopcion.DISPONIBLE).build();
		when(animalRepository.findById(10L)).thenReturn(Optional.of(alf));
		when(solicitudRepository.existsByAnimal_IdAndEmailIgnoreCaseAndEstadoIn(anyLong(), anyString(), anyList()))
				.thenReturn(false);
		when(solicitudRepository.save(any(SolicitudAdopcion.class))).thenAnswer(inv -> {
			SolicitudAdopcion sol = inv.getArgument(0);
			sol.setId(42L);
			return sol;
		});

		var req = new SolicitudAdopcionCreateRequest(
				10L,
				" Ana ",
				" ana@test.cl ",
				" +569 ",
				" Calle 1 ",
				" Maipú ",
				TipoViviendaAdopcion.CASA,
				2,
				Boolean.TRUE,
				Boolean.FALSE,
				" algo ",
				" motivación ");

		var out = service.crear(req);
		assertThat(out.id()).isEqualTo(42L);
		assertThat(out.animalNombre()).isEqualTo("Alf");
		verify(mensajeRepository).save(any(MensajeSolicitudAdopcion.class));
	}

	@Test
	void crear_ok_enAcogida() {
		Animal gato = Animal.builder().id(3L).nombre("Mish").estadoAdopcion(EstadoAdopcion.EN_ACOGIDA).build();
		when(animalRepository.findById(3L)).thenReturn(Optional.of(gato));
		when(solicitudRepository.existsByAnimal_IdAndEmailIgnoreCaseAndEstadoIn(anyLong(), anyString(), anyList()))
				.thenReturn(false);
		when(solicitudRepository.save(any(SolicitudAdopcion.class))).thenAnswer(inv -> {
			SolicitudAdopcion sol = inv.getArgument(0);
			sol.setId(9L);
			return sol;
		});

		var req = new SolicitudAdopcionCreateRequest(
				3L, "P", "p@test.cl", null, null, null, TipoViviendaAdopcion.DEPARTAMENTO, 3,
				null, null, null, "motivo largo suficiente");

		var out = service.crear(req);
		assertThat(out.id()).isEqualTo(9L);
	}

	@Test
	void agregarMensajeSolicitante_ok() {
		SolicitudAdopcion s = SolicitudAdopcion.builder()
				.id(1L)
				.estado(EstadoSolicitudAdopcion.EN_REVISION)
				.build();
		when(solicitudRepository.findByCodigoSeguimiento("abc")).thenReturn(Optional.of(s));

		service.agregarMensajeSolicitante(new MensajeSeguimientoRequest(" abc ", "  hola  "));

		verify(mensajeRepository).save(any(MensajeSolicitudAdopcion.class));
	}

	@Test
	void agregarMensajeSolicitante_codigoInvalido() {
		when(solicitudRepository.findByCodigoSeguimiento("x")).thenReturn(Optional.empty());
		assertThatThrownBy(() -> service.agregarMensajeSolicitante(new MensajeSeguimientoRequest("x", "h")))
				.isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void agregarMensajeSolicitante_solicitudCerrada() {
		SolicitudAdopcion s = SolicitudAdopcion.builder()
				.estado(EstadoSolicitudAdopcion.RECHAZADA)
				.build();
		when(solicitudRepository.findByCodigoSeguimiento("c")).thenReturn(Optional.of(s));

		assertThatThrownBy(() -> service.agregarMensajeSolicitante(new MensajeSeguimientoRequest("c", "m")))
				.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void listarParaCoordinador_mapeaFilas() {
		Instant t = Instant.parse("2026-03-01T10:00:00Z");
		Animal an = Animal.builder().id(5L).nombre("Luna").build();
		SolicitudAdopcion s = SolicitudAdopcion.builder()
				.id(11L)
				.codigoSeguimiento("CODE")
				.animal(an)
				.nombreCompleto("Sol")
				.email("sol@test.cl")
				.estado(EstadoSolicitudAdopcion.PENDIENTE)
				.createdAt(t)
				.tipoVivienda(TipoViviendaAdopcion.CASA)
				.personasEnHogar(1)
				.motivacionAdopcion("m")
				.build();
		when(solicitudRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(s));

		var list = service.listarParaCoordinador();
		assertThat(list).hasSize(1);
		assertThat(list.get(0).codigoSeguimiento()).isEqualTo("CODE");
		assertThat(list.get(0).animalNombre()).isEqualTo("Luna");
	}

	@Test
	void obtenerParaCoordinador_ok() {
		Animal an = Animal.builder().id(8L).nombre("Max").build();
		SolicitudAdopcion s = SolicitudAdopcion.builder()
				.id(20L)
				.codigoSeguimiento("Z")
				.animal(an)
				.nombreCompleto("N")
				.email("e@test.cl")
				.telefono("t")
				.direccion("d")
				.ciudad("c")
				.tipoVivienda(TipoViviendaAdopcion.CASA)
				.personasEnHogar(2)
				.tieneNinos(true)
				.tieneOtrasMascotas(false)
				.experienciaMascotas("ex")
				.motivacionAdopcion("mot")
				.estado(EstadoSolicitudAdopcion.EN_REVISION)
				.createdAt(Instant.now())
				.build();
		when(solicitudRepository.findById(20L)).thenReturn(Optional.of(s));
		when(mensajeRepository.findBySolicitudIdOrderByCreatedAtAsc(20L)).thenReturn(List.of());

		var d = service.obtenerParaCoordinador(20L);
		assertThat(d.codigoSeguimiento()).isEqualTo("Z");
		assertThat(d.mensajes()).isEmpty();
	}

	@Test
	void obtenerParaCoordinador_noExiste() {
		when(solicitudRepository.findById(1L)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> service.obtenerParaCoordinador(1L))
				.isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void agregarMensajeCoordinador_pendientePasaEnRevision() {
		SolicitudAdopcion s = SolicitudAdopcion.builder()
				.id(3L)
				.estado(EstadoSolicitudAdopcion.PENDIENTE)
				.build();
		when(solicitudRepository.findById(3L)).thenReturn(Optional.of(s));

		service.agregarMensajeCoordinador(3L, new MensajeCoordinadorRequest("  texto  "));

		assertThat(s.getEstado()).isEqualTo(EstadoSolicitudAdopcion.EN_REVISION);
		verify(mensajeRepository).save(any(MensajeSolicitudAdopcion.class));
	}

	@Test
	void agregarMensajeCoordinador_enRevision_noCambiaEstado() {
		SolicitudAdopcion s = SolicitudAdopcion.builder()
				.id(3L)
				.estado(EstadoSolicitudAdopcion.EN_REVISION)
				.build();
		when(solicitudRepository.findById(3L)).thenReturn(Optional.of(s));

		service.agregarMensajeCoordinador(3L, new MensajeCoordinadorRequest("x"));

		assertThat(s.getEstado()).isEqualTo(EstadoSolicitudAdopcion.EN_REVISION);
	}

	@Test
	void actualizarEstado_aRechazada() {
		SolicitudAdopcion s = SolicitudAdopcion.builder()
				.id(4L)
				.estado(EstadoSolicitudAdopcion.EN_REVISION)
				.animal(Animal.builder().id(1L).nombre("A").build())
				.build();
		when(solicitudRepository.findById(4L)).thenReturn(Optional.of(s));

		service.actualizarEstado(4L, new EstadoSolicitudPatchRequest(EstadoSolicitudAdopcion.RECHAZADA));

		assertThat(s.getEstado()).isEqualTo(EstadoSolicitudAdopcion.RECHAZADA);
		verify(solicitudRepository).save(s);
		verify(animalRepository, never()).findById(anyLong());
	}

	@Test
	void actualizarEstado_aprobada_anteriorAprobada_animalSinDueno_vincula() {
		Animal alf = Animal.builder().id(10L).nombre("Alf").estadoAdopcion(EstadoAdopcion.DISPONIBLE).build();
		SolicitudAdopcion s = SolicitudAdopcion.builder()
				.id(2L)
				.animal(alf)
				.nombreCompleto("X")
				.email("x@test.cl")
				.estado(EstadoSolicitudAdopcion.APROBADA)
				.build();
		when(solicitudRepository.findById(2L)).thenReturn(Optional.of(s));
		when(animalRepository.findById(10L)).thenReturn(Optional.of(alf));
		when(duenoRepository.findByEmailIgnoreCase("x@test.cl")).thenReturn(Optional.empty());
		when(duenoRepository.save(any(Dueno.class))).thenAnswer(inv -> {
			Dueno d = inv.getArgument(0);
			d.setId(3L);
			return d;
		});

		service.actualizarEstado(2L, new EstadoSolicitudPatchRequest(EstadoSolicitudAdopcion.APROBADA));

		verify(duenoRepository).save(any(Dueno.class));
		verify(animalRepository).save(any(Animal.class));
	}

	@Test
	void actualizarEstado_aprobada_reusaDuenoExistentePorEmail() {
		Dueno existente = Dueno.builder().id(50L).email("old@test.cl").nombreCompleto("Old").build();
		Animal alf = Animal.builder().id(10L).nombre("Alf").estadoAdopcion(EstadoAdopcion.DISPONIBLE).build();
		SolicitudAdopcion s = SolicitudAdopcion.builder()
				.id(2L)
				.animal(alf)
				.nombreCompleto("Nombre nuevo")
				.email("old@test.cl")
				.telefono("+1")
				.direccion("Dir")
				.ciudad("Ciudad")
				.estado(EstadoSolicitudAdopcion.EN_REVISION)
				.build();
		when(solicitudRepository.findById(2L)).thenReturn(Optional.of(s));
		when(animalRepository.findById(10L)).thenReturn(Optional.of(alf));
		when(duenoRepository.findByEmailIgnoreCase("old@test.cl")).thenReturn(Optional.of(existente));
		when(duenoRepository.save(any(Dueno.class))).thenAnswer(inv -> inv.getArgument(0));

		service.actualizarEstado(2L, new EstadoSolicitudPatchRequest(EstadoSolicitudAdopcion.APROBADA));

		assertThat(existente.getNombreCompleto()).isEqualTo("Nombre nuevo");
		verify(animalRepository).save(argThat(a -> a.getDueno() == existente));
	}

	@Test
	void actualizarDatosCoordinador_ok_actualizaEstadoYMensaje() {
		Animal a = Animal.builder().id(9L).nombre("Luna").build();
		SolicitudAdopcion s = SolicitudAdopcion.builder()
				.id(1L)
				.codigoSeguimiento("c1")
				.animal(a)
				.nombreCompleto("N")
				.email("e@test.cl")
				.tipoVivienda(TipoViviendaAdopcion.CASA)
				.personasEnHogar(1)
				.motivacionAdopcion("mot")
				.estado(EstadoSolicitudAdopcion.PENDIENTE)
				.build();
		when(solicitudRepository.findById(1L)).thenReturn(Optional.of(s));

		var req = new SolicitudAdopcionDatosPatchRequest(
				"Nombre nuevo",
				"e@test.cl",
				null,
				null,
				null,
				TipoViviendaAdopcion.DEPARTAMENTO,
				2,
				false,
				false,
				null,
				"nueva motivación suficientemente larga",
				"  Constancia del solicitante  ");

		service.actualizarDatosCoordinador(1L, req);

		assertThat(s.getEstado()).isEqualTo(EstadoSolicitudAdopcion.EN_REVISION);
		verify(mensajeRepository).save(any(MensajeSolicitudAdopcion.class));
		verify(solicitudRepository).save(s);
	}

	@Test
	void actualizarDatosCoordinador_emailDuplicado_lanza() {
		Animal a = Animal.builder().id(9L).build();
		SolicitudAdopcion s = SolicitudAdopcion.builder()
				.id(1L)
				.animal(a)
				.email("orig@test.cl")
				.estado(EstadoSolicitudAdopcion.EN_REVISION)
				.tipoVivienda(TipoViviendaAdopcion.CASA)
				.personasEnHogar(1)
				.motivacionAdopcion("m")
				.build();
		when(solicitudRepository.findById(1L)).thenReturn(Optional.of(s));
		when(solicitudRepository.existsByAnimal_IdAndEmailIgnoreCaseAndEstadoInAndIdNot(
				eq(9L), eq("otro@test.cl"), anyList(), eq(1L))).thenReturn(true);

		var req = new SolicitudAdopcionDatosPatchRequest(
				"N",
				"otro@test.cl",
				null,
				null,
				null,
				TipoViviendaAdopcion.CASA,
				1,
				false,
				false,
				null,
				"mot",
				"const");

		assertThatThrownBy(() -> service.actualizarDatosCoordinador(1L, req))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("activa");
	}

	@Test
	void obtenerVistaPublicaPorCodigo_null_lanza() {
		assertThatThrownBy(() -> service.obtenerVistaPublicaPorCodigo(null))
				.isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void obtenerVistaPublicaPorCodigo_blank_lanza() {
		assertThatThrownBy(() -> service.obtenerVistaPublicaPorCodigo("   "))
				.isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void obtenerVistaPublicaPorCodigo_ok_yDescripcionesPorEstado() {
		for (EstadoSolicitudAdopcion est : EstadoSolicitudAdopcion.values()) {
			Animal an = Animal.builder().id(1L).nombre("Pet").build();
			SolicitudAdopcion s = SolicitudAdopcion.builder()
					.id(88L)
					.codigoSeguimiento("COD")
					.animal(an)
					.nombreCompleto("S")
					.email("s@test.cl")
					.tipoVivienda(TipoViviendaAdopcion.CASA)
					.personasEnHogar(1)
					.motivacionAdopcion("m")
					.estado(est)
					.build();
			when(solicitudRepository.findByCodigoSeguimiento("COD")).thenReturn(Optional.of(s));
			when(mensajeRepository.findBySolicitudIdOrderByCreatedAtAsc(88L)).thenReturn(List.of());

			var v = service.obtenerVistaPublicaPorCodigo(" COD ");
			assertThat(v.estado()).isEqualTo(est);
			assertThat(v.estadoDescripcion()).isNotBlank();
		}
	}

	@Test
	void obtenerVistaPublica_codigoDesconocido() {
		when(solicitudRepository.findByCodigoSeguimiento("x")).thenReturn(Optional.empty());
		assertThatThrownBy(() -> service.obtenerVistaPublicaPorCodigo("x"))
				.isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void reconciliar_listaVacia_noOp() {
		when(solicitudRepository.findAprobadasConAnimalSinDueno(EstadoSolicitudAdopcion.APROBADA))
				.thenReturn(List.of());

		service.reconciliarAprobadasSinDueñoEnAnimal();

		verify(animalRepository, never()).save(any());
	}
}
