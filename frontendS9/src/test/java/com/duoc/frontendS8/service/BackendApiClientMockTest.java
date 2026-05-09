package com.duoc.frontendS8.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.duoc.frontendS8.dto.AnimalDto;
import com.duoc.frontendS8.dto.AnimalForm;
import com.duoc.frontendS8.dto.CitaDto;
import com.duoc.frontendS8.dto.CitaForm;
import com.duoc.frontendS8.dto.DuenoDto;
import com.duoc.frontendS8.dto.DuenoForm;
import com.duoc.frontendS8.dto.FacturaDto;
import com.duoc.frontendS8.dto.FacturaForm;
import com.duoc.frontendS8.dto.LineaFacturaDto;
import com.duoc.frontendS8.dto.LineaFacturaForm;
import com.duoc.frontendS8.dto.LoginApiRequest;
import com.duoc.frontendS8.dto.LoginApiResponse;
import com.duoc.frontendS8.dto.RegistroMedicoDto;
import com.duoc.frontendS8.dto.RegistroMedicoForm;
import com.duoc.frontendS8.dto.SessionUsuarioDto;
import com.duoc.frontendS8.dto.SolicitudAdopcionApiRequest;
import com.duoc.frontendS8.dto.SolicitudAdopcionCreadaDto;
import com.duoc.frontendS8.dto.SolicitudAdopcionDetalleDto;
import com.duoc.frontendS8.dto.SolicitudAdopcionListItemDto;
import com.duoc.frontendS8.dto.SolicitudDatosPatchApiRequest;
import com.duoc.frontendS8.dto.SolicitudSeguimientoVistaDto;
import com.duoc.frontendS8.dto.VeterinarioDto;
import com.duoc.frontendS8.dto.VeterinarioForm;
import com.duoc.frontendS8.exception.BackendUnavailableException;

@ExtendWith(MockitoExtension.class)
class BackendApiClientMockTest {

	@Mock
	private RestTemplate restTemplate;

	private BackendApiClient client;

	@BeforeEach
	void setUp() {
		client = new BackendApiClient(restTemplate, "http://localhost:8080/");
	}

	@Test
	void listarAnimalesConFiltros() {
		when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
				.thenReturn(ResponseEntity.ok(List.of(new AnimalDto(1L, "A", null, null, 1, null, null, null, null, null, null))));
		assertThat(client.listarAnimales(" gato ", " raza ", " stgo ", 1, 5, " M ", " DISP ")).hasSize(1);
	}

	@Test
	void obtenerAnimal() {
		when(restTemplate.getForObject("http://localhost:8080/api/animales/2", AnimalDto.class))
				.thenReturn(new AnimalDto(2L, "B", null, null, 2, null, null, null, null, null, null));
		assertThat(client.obtenerAnimal(2L).nombre()).isEqualTo("B");
	}

	@Test
	void loginOk() {
		when(restTemplate.postForObject(eq("http://localhost:8080/api/auth/login"), any(LoginApiRequest.class), eq(LoginApiResponse.class)))
				.thenReturn(new LoginApiResponse("t", "Bearer", 60, List.of("GESTOR")));
		assertThat(client.login("luis", "x").token()).isEqualTo("t");
	}

	@Test
	void login401DevuelveNull() {
		when(restTemplate.postForObject(eq("http://localhost:8080/api/auth/login"), any(LoginApiRequest.class), eq(LoginApiResponse.class)))
				.thenThrow(HttpClientErrorException.create(HttpStatus.UNAUTHORIZED, "401", null, null, null));
		assertThat(client.login("x", "y")).isNull();
	}

	@Test
	void loginRedLanzaBackendUnavailable() {
		when(restTemplate.postForObject(eq("http://localhost:8080/api/auth/login"), any(LoginApiRequest.class), eq(LoginApiResponse.class)))
				.thenThrow(new ResourceAccessException("timeout"));
		assertThatThrownBy(() -> client.login("a", "b")).isInstanceOf(BackendUnavailableException.class);
	}

	@Test
	void crearYActualizarYEliminarAnimal() {
		AnimalForm form = new AnimalForm("n", null, null, 1, null, null, null, null, null);
		client.crearAnimal("jwt", form);
		verify(restTemplate).postForEntity(eq("http://localhost:8080/api/animales"), any(HttpEntity.class), eq(AnimalDto.class));
		client.actualizarAnimal("jwt", 3L, form);
		verify(restTemplate).exchange(eq("http://localhost:8080/api/animales/3"), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Void.class));
		when(restTemplate.exchange(eq("http://localhost:8080/api/animales/3"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class)))
				.thenReturn(ResponseEntity.noContent().build());
		client.eliminarAnimal("jwt", 3L);
	}

	@Test
	void eliminarAnimalFallaSiNo2xx() {
		when(restTemplate.exchange(eq("http://localhost:8080/api/animales/9"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class)))
				.thenReturn(ResponseEntity.status(500).build());
		assertThatThrownBy(() -> client.eliminarAnimal("jwt", 9L)).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void duenosCrud() {
		when(restTemplate.exchange(eq("http://localhost:8080/api/duenos"), eq(HttpMethod.GET), any(HttpEntity.class), eq(DuenoDto[].class)))
				.thenReturn(ResponseEntity.ok(new DuenoDto[0]));
		assertThat(client.listarDuenos("jwt")).isEmpty();
		DuenoForm f = new DuenoForm("Nombre", null, null, null);
		client.crearDueno("jwt", f);
		verify(restTemplate).postForEntity(eq("http://localhost:8080/api/duenos"), any(HttpEntity.class), eq(DuenoDto.class));
		client.actualizarDueno("jwt", 1L, f);
		client.eliminarDueno("jwt", 1L);
	}

	@Test
	void veterinariosYActivosYCrud() {
		when(restTemplate.exchange(eq("http://localhost:8080/api/veterinarios"), eq(HttpMethod.GET), any(HttpEntity.class), eq(VeterinarioDto[].class)))
				.thenReturn(ResponseEntity.ok(new VeterinarioDto[0]));
		assertThat(client.listarVeterinarios("jwt")).isEmpty();
		when(restTemplate.exchange(eq("http://localhost:8080/api/veterinarios/activos"), eq(HttpMethod.GET), any(HttpEntity.class), eq(VeterinarioDto[].class)))
				.thenReturn(ResponseEntity.ok(new VeterinarioDto[0]));
		assertThat(client.listarVeterinariosActivos("jwt")).isEmpty();
		VeterinarioForm vf = new VeterinarioForm("Dr", null, true);
		client.crearVeterinario("jwt", vf);
		client.actualizarVeterinario("jwt", 2L, vf);
		client.eliminarVeterinario("jwt", 2L);
	}

	@Test
	void sesionCitasRegistrosFacturas() {
		when(restTemplate.exchange(eq("http://localhost:8080/api/auth/me"), eq(HttpMethod.GET), any(HttpEntity.class), eq(SessionUsuarioDto.class)))
				.thenReturn(ResponseEntity.ok(new SessionUsuarioDto("maria", "COORDINADOR", 1L, "Dr")));
		assertThat(client.obtenerSesion("jwt").username()).isEqualTo("maria");

		when(restTemplate.exchange(eq("http://localhost:8080/api/citas"), eq(HttpMethod.GET), any(HttpEntity.class), eq(CitaDto[].class)))
				.thenReturn(ResponseEntity.ok(new CitaDto[0]));
		assertThat(client.listarCitas("jwt")).isEmpty();
		when(restTemplate.exchange(eq("http://localhost:8080/api/citas/5"), eq(HttpMethod.GET), any(HttpEntity.class), eq(CitaDto.class)))
				.thenReturn(ResponseEntity.ok(new CitaDto(5L, 1L, "a", 2L, "v", LocalDateTime.now(), "m", "PROGRAMADA", false)));
		assertThat(client.obtenerCita("jwt", 5L).id()).isEqualTo(5L);
		CitaForm cf = new CitaForm(1L, 2L, LocalDateTime.now(), "m");
		client.crearCita("jwt", cf);
		client.cancelarCita("jwt", 5L);

		when(restTemplate.exchange(eq("http://localhost:8080/api/registros-medicos"), eq(HttpMethod.GET), any(HttpEntity.class), eq(RegistroMedicoDto[].class)))
				.thenReturn(ResponseEntity.ok(new RegistroMedicoDto[0]));
		assertThat(client.listarRegistrosMedicos("jwt")).isEmpty();
		RegistroMedicoForm rf = new RegistroMedicoForm(1L, LocalDateTime.now(), "dx", null, null, null);
		client.crearRegistroMedico("jwt", rf);

		when(restTemplate.exchange(eq("http://localhost:8080/api/facturas"), eq(HttpMethod.GET), any(HttpEntity.class), eq(FacturaDto[].class)))
				.thenReturn(ResponseEntity.ok(new FacturaDto[0]));
		assertThat(client.listarFacturas("jwt")).isEmpty();
		when(restTemplate.exchange(eq("http://localhost:8080/api/facturas/8"), eq(HttpMethod.GET), any(HttpEntity.class), eq(FacturaDto.class)))
				.thenReturn(ResponseEntity.ok(new FacturaDto(8L, "F-1", null, BigDecimal.ONE, 1L, 1L, "a", List.of(new LineaFacturaDto(1L, "SERVICIO", "x", BigDecimal.ONE)))));
		assertThat(client.obtenerFactura("jwt", 8L).id()).isEqualTo(8L);
		FacturaForm ff = new FacturaForm(1L, List.of(new LineaFacturaForm("SERVICIO", "visita", BigDecimal.TEN)));
		client.crearFactura("jwt", ff);
		client.enviarFacturaCorreo("jwt", 8L, "a@b.cl");
		verify(restTemplate).postForEntity(
				eq("http://localhost:8080/api/facturas/8/enviar-correo"),
				any(HttpEntity.class),
				eq(Void.class));
	}

	@Test
	void obtenerRegistroMedicoNotFound() {
		when(restTemplate.exchange(
				eq("http://localhost:8080/api/registros-medicos/99"),
				eq(HttpMethod.GET),
				any(HttpEntity.class),
				eq(RegistroMedicoDto.class)))
				.thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "nf", null, null, null));
		assertThat(client.obtenerRegistroMedico("jwt", 99L)).isNull();
	}

	@Test
	void obtenerRegistroMedico_redDevuelveNull() {
		when(restTemplate.exchange(
				eq("http://localhost:8080/api/registros-medicos/7"),
				eq(HttpMethod.GET),
				any(HttpEntity.class),
				eq(RegistroMedicoDto.class)))
				.thenThrow(new ResourceAccessException("timeout"));
		assertThat(client.obtenerRegistroMedico("jwt", 7L)).isNull();
	}

	@Test
	void listarAnimales_bodyNull_devuelveVacio() {
		when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
				.thenReturn(ResponseEntity.ok(null));
		assertThat(client.listarAnimales(null, null, null, null, null, null, null)).isEmpty();
	}

	@Test
	void login_errorDistintoDe401_propaga() {
		when(restTemplate.postForObject(eq("http://localhost:8080/api/auth/login"), any(LoginApiRequest.class), eq(LoginApiResponse.class)))
				.thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "bad", null, null, null));
		assertThatThrownBy(() -> client.login("u", "p")).isInstanceOf(HttpClientErrorException.class);
	}

	@Test
	void consultarSeguimientoPublico_nullOSoloEspacios() {
		assertThat(client.consultarSeguimientoPublico(null)).isNull();
		assertThat(client.consultarSeguimientoPublico(" \t ")).isNull();
	}

	@Test
	void consultarSeguimientoPublico_okYNotFound() {
		when(restTemplate.getForObject(any(URI.class), eq(SolicitudSeguimientoVistaDto.class)))
				.thenReturn(new SolicitudSeguimientoVistaDto("C", "PENDIENTE", "d", "Pet", List.of()));
		assertThat(client.consultarSeguimientoPublico(" abc ").codigoSeguimiento()).isEqualTo("C");

		when(restTemplate.getForObject(any(URI.class), eq(SolicitudSeguimientoVistaDto.class)))
				.thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "nf", null, null, null));
		assertThat(client.consultarSeguimientoPublico("x")).isNull();
	}

	@Test
	void adopcionesApiCompleta() {
		when(restTemplate.postForObject(
				eq("http://localhost:8080/api/adopciones/solicitudes"),
				any(HttpEntity.class),
				eq(SolicitudAdopcionCreadaDto.class)))
				.thenReturn(new SolicitudAdopcionCreadaDto(1L, "CODE", "Pet", "ok"));
		var req = new SolicitudAdopcionApiRequest(
				1L, "N", "n@test.cl", null, null, null, "CASA", 2, false, false, null, "mot");
		assertThat(client.crearSolicitudAdopcionPublica(req).codigoSeguimiento()).isEqualTo("CODE");

		when(restTemplate.postForEntity(
				eq("http://localhost:8080/api/adopciones/seguimiento/mensajes"),
				any(HttpEntity.class),
				eq(Void.class)))
				.thenReturn(ResponseEntity.noContent().build());
		client.enviarMensajeSeguimientoAdopcion(" cod ", "  txt  ");

		when(restTemplate.exchange(
				eq("http://localhost:8080/api/adopciones/solicitudes"),
				eq(HttpMethod.GET),
				any(HttpEntity.class),
				eq(SolicitudAdopcionListItemDto[].class)))
				.thenReturn(ResponseEntity.ok(new SolicitudAdopcionListItemDto[] {
						new SolicitudAdopcionListItemDto(1L, "C", 2L, "A", "Sol", "e@test.cl", "PENDIENTE", null)
				}));
		assertThat(client.listarSolicitudesAdopcion("jwt")).hasSize(1);

		when(restTemplate.exchange(
				eq("http://localhost:8080/api/adopciones/solicitudes/3"),
				eq(HttpMethod.GET),
				any(HttpEntity.class),
				eq(SolicitudAdopcionDetalleDto.class)))
				.thenReturn(ResponseEntity.ok(new SolicitudAdopcionDetalleDto(
						3L, "Z", 1L, "Dog", "N", "e@test.cl", null, null, null, "CASA", 1,
						false, false, null, "m", "PENDIENTE", null, List.of())));
		assertThat(client.obtenerSolicitudAdopcion("jwt", 3L).id()).isEqualTo(3L);

		when(restTemplate.postForEntity(
				eq("http://localhost:8080/api/adopciones/solicitudes/3/mensajes"),
				any(HttpEntity.class),
				eq(Void.class)))
				.thenReturn(ResponseEntity.noContent().build());
		client.coordinadorEnviarMensajeAdopcion("jwt", 3L, " hola ");

		verify(restTemplate).postForEntity(
				eq("http://localhost:8080/api/adopciones/solicitudes/3/mensajes"),
				any(HttpEntity.class),
				eq(Void.class));

		when(restTemplate.exchange(
				eq("http://localhost:8080/api/adopciones/solicitudes/3/estado"),
				eq(HttpMethod.PATCH),
				any(HttpEntity.class),
				eq(Void.class)))
				.thenReturn(ResponseEntity.noContent().build());
		client.coordinadorActualizarEstadoSolicitud("jwt", 3L, "APROBADA");

		when(restTemplate.exchange(
				eq("http://localhost:8080/api/adopciones/solicitudes/3"),
				eq(HttpMethod.DELETE),
				any(HttpEntity.class),
				eq(Void.class)))
				.thenReturn(ResponseEntity.noContent().build());
		client.eliminarSolicitudAdopcion("jwt", 3L);

		when(restTemplate.exchange(
				eq("http://localhost:8080/api/adopciones/solicitudes/3/datos"),
				eq(HttpMethod.PATCH),
				any(HttpEntity.class),
				eq(Void.class)))
				.thenReturn(ResponseEntity.noContent().build());
		var patch = new SolicitudDatosPatchApiRequest(
				"N", "e@test.cl", null, null, null, "CASA", 1, false, false, null, "m", "constancia ok");
		client.coordinadorActualizarDatosSolicitud("jwt", 3L, patch);

		verify(restTemplate).exchange(
				eq("http://localhost:8080/api/adopciones/solicitudes/3/estado"),
				eq(HttpMethod.PATCH),
				any(HttpEntity.class),
				eq(Void.class));
	}
}
