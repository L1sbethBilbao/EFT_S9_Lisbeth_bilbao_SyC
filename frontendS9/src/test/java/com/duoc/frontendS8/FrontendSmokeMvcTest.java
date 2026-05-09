package com.duoc.frontendS8;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.duoc.frontendS8.dto.AnimalDto;
import com.duoc.frontendS8.dto.CitaDto;
import com.duoc.frontendS8.dto.DuenoDto;
import com.duoc.frontendS8.dto.FacturaDto;
import com.duoc.frontendS8.dto.RegistroMedicoDto;
import com.duoc.frontendS8.dto.LoginApiResponse;
import com.duoc.frontendS8.dto.SessionUsuarioDto;
import com.duoc.frontendS8.dto.SolicitudAdopcionCreadaDto;
import com.duoc.frontendS8.dto.SolicitudAdopcionDetalleDto;
import com.duoc.frontendS8.dto.SolicitudAdopcionListItemDto;
import com.duoc.frontendS8.dto.SolicitudSeguimientoVistaDto;
import com.duoc.frontendS8.dto.VeterinarioDto;
import com.duoc.frontendS8.service.BackendApiClient;

@SpringBootTest
@AutoConfigureMockMvc
class FrontendSmokeMvcTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private BackendApiClient backendApiClient;

	private void stubApiBasico() {
		when(backendApiClient.listarAnimales(any(), any(), any(), any(), any(), any(), any()))
				.thenReturn(List.of(new AnimalDto(1L, "A", null, null, 1, null, null, null, null, null, null)));
		when(backendApiClient.obtenerAnimal(anyLong())).thenAnswer(inv -> new AnimalDto(inv.getArgument(0), "A", null, null, 1, null, null, null, null, null, null));
		when(backendApiClient.listarDuenos(any())).thenReturn(List.of(new DuenoDto(1L, "Nombre", null, null, null)));
		when(backendApiClient.listarVeterinarios(any())).thenReturn(List.of(new VeterinarioDto(1L, "Dr", null, true)));
		when(backendApiClient.listarVeterinariosActivos(any())).thenReturn(List.of(new VeterinarioDto(1L, "Dr", null, true)));
		when(backendApiClient.obtenerSesion(any())).thenReturn(new SessionUsuarioDto("maria", "COORDINADOR", 1L, "Dr"));
		when(backendApiClient.listarCitas(any())).thenReturn(List.of(new CitaDto(1L, 1L, "a", 1L, "v", LocalDateTime.now(), "m", "PROGRAMADA", false)));
		when(backendApiClient.obtenerCita(any(), anyLong())).thenReturn(new CitaDto(1L, 1L, "a", 1L, "v", LocalDateTime.now(), "m", "PROGRAMADA", false));
		when(backendApiClient.listarRegistrosMedicos(any())).thenReturn(List.of(new RegistroMedicoDto(1L, 1L, LocalDateTime.now(), "d", null, null, null, null)));
		when(backendApiClient.listarFacturas(any())).thenReturn(List.of(new FacturaDto(1L, "F-1", LocalDate.now(), BigDecimal.ONE, 1L, 1L, "a", List.of())));
		when(backendApiClient.obtenerFactura(any(), anyLong())).thenReturn(new FacturaDto(1L, "F-1", LocalDate.now(), BigDecimal.ONE, 1L, 1L, "a", List.of()));
		when(backendApiClient.obtenerRegistroMedico(any(), eq(99L))).thenReturn(
				new RegistroMedicoDto(99L, 1L, LocalDateTime.now(), "Diagnóstico largo para probar truncado en factura", "trat", "meds", null, null));
		when(backendApiClient.obtenerRegistroMedico(any(), eq(98L))).thenReturn(null);
		doNothing().when(backendApiClient).crearAnimal(any(), any());
		doNothing().when(backendApiClient).actualizarAnimal(any(), anyLong(), any());
		doNothing().when(backendApiClient).eliminarAnimal(any(), anyLong());
		doNothing().when(backendApiClient).crearDueno(any(), any());
		doNothing().when(backendApiClient).actualizarDueno(any(), anyLong(), any());
		doNothing().when(backendApiClient).eliminarDueno(any(), anyLong());
		doNothing().when(backendApiClient).crearVeterinario(any(), any());
		doNothing().when(backendApiClient).actualizarVeterinario(any(), anyLong(), any());
		doNothing().when(backendApiClient).eliminarVeterinario(any(), anyLong());
		doNothing().when(backendApiClient).crearCita(any(), any());
		doNothing().when(backendApiClient).cancelarCita(any(), anyLong());
		doNothing().when(backendApiClient).crearRegistroMedico(any(), any());
		doNothing().when(backendApiClient).crearFactura(any(), any());
		doNothing().when(backendApiClient).enviarFacturaCorreo(any(), anyLong(), any());
	}

	@Test
	void publicasYLogin() throws Exception {
		stubApiBasico();
		mockMvc.perform(get("/")).andExpect(status().isOk());
		mockMvc.perform(get("/login")).andExpect(status().isOk());
		mockMvc.perform(get("/mascotas/1")).andExpect(status().isOk());
	}

	@Test
	void catalogo_omiteFiltroMarcadoPeligroso() throws Exception {
		stubApiBasico();
		ArgumentCaptor<String> especieCap = ArgumentCaptor.forClass(String.class);
		mockMvc.perform(get("/").param("especie", "<img src=x onerror=alert(1)>"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("filtroSeguridadAviso"));
		verify(backendApiClient).listarAnimales(especieCap.capture(), any(), any(), any(), any(), any(), any());
		Assertions.assertNull(especieCap.getValue());
	}

	@Test
	void adopcion_rechazaNombreConMarkup_noLlamaApi() throws Exception {
		stubApiBasico();
		mockMvc.perform(post("/adopciones/solicitar").with(csrf())
				.param("animalId", "1")
				.param("nombreCompleto", "<b>XSS</b>")
				.param("email", "x@test.cl")
				.param("tipoVivienda", "CASA")
				.param("personasEnHogar", "2")
				.param("motivacionAdopcion", "Motivación suficientemente larga para superar validaciones"))
				.andExpect(status().isOk());
		verify(backendApiClient, never()).crearSolicitudAdopcionPublica(any());
	}

	@Test
	void seguimiento_codigoConMarkup_noConsultaApi() throws Exception {
		stubApiBasico();
		mockMvc.perform(get("/adopciones/seguimiento").param("codigo", "<script>bad</script>"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("consultaError"));
		verify(backendApiClient, never()).consultarSeguimientoPublico(any());
	}

	@Test
	void seguimientoSalirRedirigeSinCodigo() throws Exception {
		stubApiBasico();
		mockMvc.perform(get("/adopciones/seguimiento/salir"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/adopciones/seguimiento"));
	}

	@Test
	void duenosConSesionJwt() throws Exception {
		stubApiBasico();
		mockMvc.perform(get("/duenos")
				.sessionAttr(BackendApiClient.SESSION_JWT, "jwt")
				.with(user("maria").roles("COORDINADOR", "GESTOR")))
				.andExpect(status().isOk());
	}

	@Test
	void veterinariosCitasFichasFacturas() throws Exception {
		stubApiBasico();
		var auth = user("pedro").roles("COORDINADOR", "VETERINARIO");
		mockMvc.perform(get("/veterinarios").sessionAttr(BackendApiClient.SESSION_JWT, "jwt").with(auth)).andExpect(status().isOk());
		mockMvc.perform(get("/citas").sessionAttr(BackendApiClient.SESSION_JWT, "jwt").with(auth)).andExpect(status().isOk());
		mockMvc.perform(get("/registros-medicos").sessionAttr(BackendApiClient.SESSION_JWT, "jwt").with(auth)).andExpect(status().isOk());
		mockMvc.perform(get("/facturas").sessionAttr(BackendApiClient.SESSION_JWT, "jwt").with(auth)).andExpect(status().isOk());
		mockMvc.perform(get("/facturas/1").sessionAttr(BackendApiClient.SESSION_JWT, "jwt").with(auth)).andExpect(status().isOk());
	}

	@Test
	void formulariosAltasYEdicion() throws Exception {
		stubApiBasico();
		var gestor = user("luis").roles("COORDINADOR", "GESTOR");
		mockMvc.perform(get("/animales/nuevo").sessionAttr(BackendApiClient.SESSION_JWT, "jwt").with(gestor)).andExpect(status().isOk());
		mockMvc.perform(get("/animales/1/editar").sessionAttr(BackendApiClient.SESSION_JWT, "jwt").with(gestor)).andExpect(status().isOk());
		mockMvc.perform(get("/duenos/nuevo").sessionAttr(BackendApiClient.SESSION_JWT, "jwt").with(gestor)).andExpect(status().isOk());
		mockMvc.perform(get("/duenos/1/editar").sessionAttr(BackendApiClient.SESSION_JWT, "jwt").with(gestor)).andExpect(status().isOk());
		var coord = user("maria").roles("COORDINADOR");
		mockMvc.perform(get("/veterinarios/nuevo").sessionAttr(BackendApiClient.SESSION_JWT, "jwt").with(coord)).andExpect(status().isOk());
		mockMvc.perform(get("/veterinarios/1/editar").sessionAttr(BackendApiClient.SESSION_JWT, "jwt").with(coord)).andExpect(status().isOk());
		mockMvc.perform(get("/citas/nueva").sessionAttr(BackendApiClient.SESSION_JWT, "jwt").with(coord)).andExpect(status().isOk());
		mockMvc.perform(get("/registros-medicos/nuevo").param("citaId", "1").sessionAttr(BackendApiClient.SESSION_JWT, "jwt").with(coord)).andExpect(status().isOk());
		mockMvc.perform(get("/facturas/nueva").param("registroMedicoId", "98").sessionAttr(BackendApiClient.SESSION_JWT, "jwt").with(coord)).andExpect(status().isOk());
		mockMvc.perform(get("/facturas/nueva").param("registroMedicoId", "99").sessionAttr(BackendApiClient.SESSION_JWT, "jwt").with(coord)).andExpect(status().isOk());
	}

	@Test
	void citasNuevaVeterinarioSinElegir() throws Exception {
		stubApiBasico();
		when(backendApiClient.obtenerSesion(any())).thenReturn(new SessionUsuarioDto("pedro", "VETERINARIO", 5L, "Dr House"));
		mockMvc.perform(get("/citas/nueva").sessionAttr(BackendApiClient.SESSION_JWT, "jwt").with(user("pedro").roles("VETERINARIO"))).andExpect(status().isOk());
	}

	@Test
	void citasNuevaVeterinarioSinPerfilClinico() throws Exception {
		stubApiBasico();
		when(backendApiClient.obtenerSesion(any())).thenReturn(new SessionUsuarioDto("ana", "VETERINARIO", null, null));
		mockMvc.perform(get("/citas/nueva").sessionAttr(BackendApiClient.SESSION_JWT, "jwt").with(user("ana").roles("VETERINARIO"))).andExpect(status().isOk());
	}

	@Test
	void loginGetYPost() throws Exception {
		stubApiBasico();
		mockMvc.perform(get("/login")).andExpect(status().isOk());
		mockMvc.perform(get("/login").param("error", "")).andExpect(status().isOk());
		mockMvc.perform(get("/login").param("backend", "")).andExpect(status().isOk());
		when(backendApiClient.login("maria", "ok")).thenReturn(new LoginApiResponse("tok", "Bearer", 60, List.of("GESTOR")));
		mockMvc.perform(post("/login").param("username", "maria").param("password", "ok").with(csrf())).andExpect(status().is3xxRedirection());
	}

	@Test
	void catalogo_cuandoBackendNoResponde_muestraListasVacias() throws Exception {
		when(backendApiClient.listarAnimales(any(), any(), any(), any(), any(), any(), any()))
				.thenThrow(new ResourceAccessException("sin conexión"));
		mockMvc.perform(get("/")).andExpect(status().isOk());
		mockMvc.perform(get("/adopciones/disponibles")).andExpect(status().isOk());
	}

	@Test
	void adopcionesPublicasYGestionCoordinador_flujoMvc() throws Exception {
		stubApiBasico();
		when(backendApiClient.crearSolicitudAdopcionPublica(any())).thenReturn(
				new SolicitudAdopcionCreadaDto(1L, "CODE", "Pet", "ok"));
		when(backendApiClient.consultarSeguimientoPublico(any())).thenReturn(
				new SolicitudSeguimientoVistaDto("K", "PENDIENTE", "d", "Dog", List.of()));

		mockMvc.perform(get("/adopciones/solicitar").param("animalId", "1")).andExpect(status().isOk());

		mockMvc.perform(post("/adopciones/solicitar").with(csrf())
				.param("animalId", "1")
				.param("nombreCompleto", "Ana Test")
				.param("email", "ana@test.cl")
				.param("tipoVivienda", "CASA")
				.param("personasEnHogar", "2")
				.param("motivacionAdopcion", "Quiero adoptar por motivos válidos de prueba suficientemente largos"))
				.andExpect(status().is3xxRedirection());

		mockMvc.perform(get("/adopciones/seguimiento").param("codigo", "XYZ")).andExpect(status().isOk());

		mockMvc.perform(post("/adopciones/seguimiento/mensaje").with(csrf())
				.param("codigoSeguimiento", "ABC")
				.param("cuerpo", "Mensaje de prueba para coordinación"))
				.andExpect(status().is3xxRedirection());

		var detalle = new SolicitudAdopcionDetalleDto(
				1L, "C", 1L, "Dog", "Nombre", "e@test.cl", null, null, null, "CASA", 2,
				false, false, null, "motivo largo", "PENDIENTE", Instant.now(), List.of());
		when(backendApiClient.listarSolicitudesAdopcion(any())).thenReturn(List.of(
				new SolicitudAdopcionListItemDto(1L, "C", 1L, "Dog", "Nombre", "e@test.cl", "PENDIENTE", Instant.now())));
		when(backendApiClient.obtenerSolicitudAdopcion(any(), eq(1L))).thenReturn(detalle);

		var coord = user("maria").roles("COORDINADOR");
		var jwt = "jwt";
		mockMvc.perform(get("/adopciones/gestion").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(coord)).andExpect(status().isOk());
		mockMvc.perform(get("/adopciones/gestion/1").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(coord)).andExpect(status().isOk());

		mockMvc.perform(post("/adopciones/gestion/1/mensaje").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(coord).with(csrf())
				.param("cuerpo", "Respuesta del coordinador"))
				.andExpect(status().is3xxRedirection());

		mockMvc.perform(post("/adopciones/gestion/1/estado").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(coord).with(csrf())
				.param("estado", "EN_REVISION"))
				.andExpect(status().is3xxRedirection());

		mockMvc.perform(post("/adopciones/gestion/1/datos").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(coord).with(csrf())
				.param("nombreCompleto", "Nombre Actualizado")
				.param("email", "e@test.cl")
				.param("tipoVivienda", "CASA")
				.param("personasEnHogar", "2")
				.param("motivacionAdopcion", "Motivo actualizado suficientemente largo para validación")
				.param("constanciaSolicitante", "Constancia del solicitante para corrección de datos en evaluación final"))
				.andExpect(status().is3xxRedirection());

		mockMvc.perform(post("/adopciones/gestion/1/eliminar").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(coord).with(csrf()))
				.andExpect(status().is3xxRedirection());
	}

	@Test
	void adopciones_publicas_validacion_yErroresHttp() throws Exception {
		stubApiBasico();
		mockMvc.perform(post("/adopciones/solicitar").with(csrf())
				.param("animalId", "1")
				.param("nombreCompleto", "")
				.param("email", "no-es-email")
				.param("tipoVivienda", "CASA")
				.param("personasEnHogar", "1")
				.param("motivacionAdopcion", "corta"))
				.andExpect(status().isOk());

		when(backendApiClient.crearSolicitudAdopcionPublica(any())).thenThrow(HttpClientErrorException.BadRequest.create(
				HttpStatus.BAD_REQUEST,
				"Bad Request",
				HttpHeaders.EMPTY,
				"{\"message\":\"Correo duplicado para esta mascota\"}".getBytes(StandardCharsets.UTF_8),
				StandardCharsets.UTF_8));
		mockMvc.perform(post("/adopciones/solicitar").with(csrf())
				.param("animalId", "1")
				.param("nombreCompleto", "Nombre Completo Válido")
				.param("email", "dup@test.cl")
				.param("tipoVivienda", "CASA")
				.param("personasEnHogar", "2")
				.param("motivacionAdopcion", "Motivación suficientemente larga para superar validaciones"))
				.andExpect(status().isOk());

		stubApiBasico();
		doThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "nf", null, null, null))
				.when(backendApiClient).enviarMensajeSeguimientoAdopcion(any(), any());
		mockMvc.perform(post("/adopciones/seguimiento/mensaje").with(csrf())
				.param("codigoSeguimiento", "COD")
				.param("cuerpo", "Contenido del mensaje de seguimiento"))
				.andExpect(status().isOk());

		stubApiBasico();
		doThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "br", null, null, null))
				.when(backendApiClient).enviarMensajeSeguimientoAdopcion(any(), any());
		mockMvc.perform(post("/adopciones/seguimiento/mensaje").with(csrf())
				.param("codigoSeguimiento", "COD")
				.param("cuerpo", "Otro contenido"))
				.andExpect(status().isOk());

		stubApiBasico();
		when(backendApiClient.obtenerAnimal(99L)).thenThrow(new ResourceAccessException("off"));
		mockMvc.perform(get("/adopciones/solicitar").param("animalId", "99")).andExpect(status().isOk());
	}

	@Test
	void adopciones_solicitud_sinBackend_alEnviar() throws Exception {
		stubApiBasico();
		when(backendApiClient.crearSolicitudAdopcionPublica(any())).thenThrow(new ResourceAccessException("api"));
		mockMvc.perform(post("/adopciones/solicitar").with(csrf())
				.param("animalId", "1")
				.param("nombreCompleto", "Nombre Post OK")
				.param("email", "postok@test.cl")
				.param("tipoVivienda", "CASA")
				.param("personasEnHogar", "2")
				.param("motivacionAdopcion", "Motivación larga para cubrir rama de error de red al enviar"))
				.andExpect(status().isOk());
	}

	@Test
	void adopciones_gracias_sinFlash_redirige() throws Exception {
		mockMvc.perform(get("/adopciones/gracias")).andExpect(status().is3xxRedirection());
	}

	@Test
	void adopciones_seguimiento_mensajeValidacion() throws Exception {
		stubApiBasico();
		mockMvc.perform(post("/adopciones/seguimiento/mensaje").with(csrf())
				.param("codigoSeguimiento", "")
				.param("cuerpo", ""))
				.andExpect(status().isOk());
	}

	@Test
	void adopciones_gestion_mensajeCoordinadorValidacion() throws Exception {
		stubApiBasico();
		mockMvc.perform(post("/adopciones/gestion/1/mensaje").sessionAttr(BackendApiClient.SESSION_JWT, "jwt")
				.with(user("maria").roles("COORDINADOR")).with(csrf())
				.param("cuerpo", ""))
				.andExpect(status().is3xxRedirection());
	}

	@Test
	void adopciones_gestion_backendErrores() throws Exception {
		stubApiBasico();
		when(backendApiClient.listarSolicitudesAdopcion(any())).thenThrow(new ResourceAccessException("x"));
		mockMvc.perform(get("/adopciones/gestion").sessionAttr(BackendApiClient.SESSION_JWT, "jwt")
				.with(user("maria").roles("COORDINADOR"))).andExpect(status().isOk());

		stubApiBasico();
		when(backendApiClient.obtenerSolicitudAdopcion(any(), eq(77L))).thenThrow(new ResourceAccessException("x"));
		mockMvc.perform(get("/adopciones/gestion/77").sessionAttr(BackendApiClient.SESSION_JWT, "jwt")
				.with(user("maria").roles("COORDINADOR"))).andExpect(status().is3xxRedirection());

		stubApiBasico();
		var detalle = new SolicitudAdopcionDetalleDto(
				2L, "Z", 1L, "Dog", "N", "e@test.cl", null, null, null, "CASA", 2,
				false, false, null, "motivo largo para formulario", "PENDIENTE", Instant.now(), List.of());
		when(backendApiClient.obtenerSolicitudAdopcion(any(), eq(2L))).thenReturn(detalle);
		doThrow(HttpClientErrorException.BadRequest.create(
				HttpStatus.BAD_REQUEST,
				"Bad Request",
				HttpHeaders.EMPTY,
				"{\"message\":\"Email conflictivo\"}".getBytes(StandardCharsets.UTF_8),
				StandardCharsets.UTF_8)).when(backendApiClient).coordinadorActualizarDatosSolicitud(any(), eq(2L), any());
		mockMvc.perform(post("/adopciones/gestion/2/datos").sessionAttr(BackendApiClient.SESSION_JWT, "jwt")
				.with(user("maria").roles("COORDINADOR")).with(csrf())
				.param("nombreCompleto", "Nombre")
				.param("email", "e@test.cl")
				.param("tipoVivienda", "CASA")
				.param("personasEnHogar", "2")
				.param("motivacionAdopcion", "Motivo actualizado suficientemente largo")
				.param("constanciaSolicitante", "Constancia mínima diez caracteres para validar edición"))
				.andExpect(status().isOk());

		stubApiBasico();
		when(backendApiClient.obtenerSolicitudAdopcion(any(), eq(2L))).thenReturn(detalle);
		doThrow(new ResourceAccessException("red")).when(backendApiClient).coordinadorActualizarDatosSolicitud(any(), eq(2L), any());
		mockMvc.perform(post("/adopciones/gestion/2/datos").sessionAttr(BackendApiClient.SESSION_JWT, "jwt")
				.with(user("maria").roles("COORDINADOR")).with(csrf())
				.param("nombreCompleto", "Nombre")
				.param("email", "e@test.cl")
				.param("tipoVivienda", "CASA")
				.param("personasEnHogar", "2")
				.param("motivacionAdopcion", "Motivo actualizado suficientemente largo")
				.param("constanciaSolicitante", "Constancia mínima diez caracteres para validar edición"))
				.andExpect(status().isOk());

		stubApiBasico();
		doThrow(new ResourceAccessException("del")).when(backendApiClient).eliminarSolicitudAdopcion(any(), eq(4L));
		mockMvc.perform(post("/adopciones/gestion/4/eliminar").sessionAttr(BackendApiClient.SESSION_JWT, "jwt")
				.with(user("maria").roles("COORDINADOR")).with(csrf())).andExpect(status().is3xxRedirection());
	}

	@Test
	void postsAnimalesDuenosVeterinarios() throws Exception {
		stubApiBasico();
		var gestor = user("g1").roles("COORDINADOR", "GESTOR");
		var jwt = "jwt";
		mockMvc.perform(post("/animales").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(gestor).with(csrf())
				.param("nombre", "Lulu")
				.param("especie", "perro")
				.param("raza", "")
				.param("edad", "2")
				.param("ubicacion", "Stgo")
				.param("genero", "FEMENINO")
				.param("estadoAdopcion", "DISPONIBLE")
				.param("fotoUrl", ""))
				.andExpect(status().is3xxRedirection());
		mockMvc.perform(post("/animales/1/actualizar").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(gestor).with(csrf())
				.param("nombre", "Luna2")
				.param("especie", "gato")
				.param("raza", "")
				.param("edad", "3")
				.param("ubicacion", "")
				.param("genero", "")
				.param("estadoAdopcion", "")
				.param("fotoUrl", ""))
				.andExpect(status().is3xxRedirection());
		mockMvc.perform(post("/animales/1/eliminar").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(gestor).with(csrf()))
				.andExpect(status().is3xxRedirection());
		mockMvc.perform(post("/duenos").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(gestor).with(csrf())
				.param("nombreCompleto", "Ana Pérez")
				.param("email", "a@b.cl")
				.param("telefono", "")
				.param("direccion", ""))
				.andExpect(status().is3xxRedirection());
		mockMvc.perform(post("/duenos/1/actualizar").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(gestor).with(csrf())
				.param("nombreCompleto", "Ana P.")
				.param("email", "")
				.param("telefono", "")
				.param("direccion", ""))
				.andExpect(status().is3xxRedirection());
		mockMvc.perform(post("/duenos/1/eliminar").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(gestor).with(csrf()))
				.andExpect(status().is3xxRedirection());
		var coord = user("c1").roles("COORDINADOR");
		mockMvc.perform(post("/veterinarios").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(coord).with(csrf())
				.param("nombre", "Dr Test")
				.param("especialidad", "General")
				.param("activo", "true"))
				.andExpect(status().is3xxRedirection());
		mockMvc.perform(post("/veterinarios/1/actualizar").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(coord).with(csrf())
				.param("nombre", "Dr Test2")
				.param("especialidad", "Cirugía")
				.param("activo", "true"))
				.andExpect(status().is3xxRedirection());
		mockMvc.perform(post("/veterinarios/1/eliminar").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(coord).with(csrf()))
				.andExpect(status().is3xxRedirection());
		mockMvc.perform(post("/animales").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(gestor).with(csrf())
				.param("nombre", "")
				.param("especie", "x")
				.param("raza", "")
				.param("edad", "0")
				.param("ubicacion", "")
				.param("genero", "")
				.param("estadoAdopcion", "")
				.param("fotoUrl", ""))
				.andExpect(status().isOk());
	}

	@Test
	void postsCitasRegistrosFacturasYErroresValidacion() throws Exception {
		stubApiBasico();
		var jwt = "jwt";
		var auth = user("mix").roles("COORDINADOR", "VETERINARIO");
		LocalDate d = LocalDate.now().plusDays(2);
		mockMvc.perform(post("/citas").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(auth).with(csrf())
				.param("animalId", "1")
				.param("veterinarioId", "1")
				.param("fecha", d.toString())
				.param("hora", LocalTime.of(11, 30).toString())
				.param("motivo", "control"))
				.andExpect(status().is3xxRedirection());
		mockMvc.perform(post("/citas/1/cancelar").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(auth).with(csrf()))
				.andExpect(status().is3xxRedirection());
		mockMvc.perform(post("/citas").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(auth).with(csrf())
				.param("animalId", "1")
				.param("veterinarioId", "1")
				.param("fecha", d.toString())
				.param("motivo", "falta hora"))
				.andExpect(status().isOk());
		mockMvc.perform(post("/registros-medicos").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(auth).with(csrf())
				.param("citaId", "1")
				.param("fecha", d.toString())
				.param("hora", LocalTime.of(11, 0).toString())
				.param("diagnostico", "Dx OK")
				.param("tratamiento", "t")
				.param("medicamentos", "m")
				.param("notas", "n"))
				.andExpect(status().is3xxRedirection());
		mockMvc.perform(post("/registros-medicos").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(auth).with(csrf())
				.param("citaId", "1")
				.param("fecha", d.toString())
				.param("hora", LocalTime.of(11, 0).toString())
				.param("diagnostico", "")
				.param("tratamiento", "")
				.param("medicamentos", "")
				.param("notas", ""))
				.andExpect(status().isOk());
		mockMvc.perform(post("/facturas").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(auth).with(csrf())
				.param("registroMedicoId", "99")
				.param("lineas[0].tipo", "SERVICIO")
				.param("lineas[0].descripcion", "Consulta")
				.param("lineas[0].monto", "10000")
				.param("lineas[1].tipo", "MEDICAMENTO")
				.param("lineas[1].descripcion", "Meds")
				.param("lineas[1].monto", "5000"))
				.andExpect(status().is3xxRedirection());
		mockMvc.perform(post("/facturas/1/enviar-correo").sessionAttr(BackendApiClient.SESSION_JWT, jwt).with(auth).with(csrf())
				.param("email", "cli@ejemplo.cl"))
				.andExpect(status().is3xxRedirection());
	}
}
