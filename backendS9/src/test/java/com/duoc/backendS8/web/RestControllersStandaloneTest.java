package com.duoc.backendS8.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.duoc.backendS8.dto.CitaResponse;
import com.duoc.backendS8.dto.DuenoResponse;
import com.duoc.backendS8.dto.FacturaResponse;
import com.duoc.backendS8.dto.RegistroMedicoResponse;
import com.duoc.backendS8.dto.VeterinarioResponse;
import com.duoc.backendS8.entity.EstadoCita;
import com.duoc.backendS8.service.CitaService;
import com.duoc.backendS8.service.DuenoService;
import com.duoc.backendS8.service.FacturaService;
import com.duoc.backendS8.service.RegistroMedicoService;
import com.duoc.backendS8.service.VeterinarioService;

@ExtendWith(MockitoExtension.class)
class RestControllersStandaloneTest {

	@Mock
	private VeterinarioService veterinarioService;
	@Mock
	private DuenoService duenoService;
	@Mock
	private CitaService citaService;
	@Mock
	private RegistroMedicoService registroMedicoService;
	@Mock
	private FacturaService facturaService;
	private MockMvc veterinariosMvc;
	private MockMvc duenosMvc;
	private MockMvc citasMvc;
	private MockMvc registrosMvc;
	private MockMvc facturasMvc;

	@BeforeEach
	void setup() {
		veterinariosMvc = MockMvcBuilders.standaloneSetup(new VeterinarioController(veterinarioService)).build();
		duenosMvc = MockMvcBuilders.standaloneSetup(new DuenoController(duenoService)).build();
		citasMvc = MockMvcBuilders.standaloneSetup(new CitaController(citaService)).build();
		registrosMvc = MockMvcBuilders.standaloneSetup(new RegistroMedicoController(registroMedicoService)).build();
		facturasMvc = MockMvcBuilders.standaloneSetup(new FacturaController(facturaService)).build();
	}

	@AfterEach
	void clear() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void veterinariosCrudLectura() throws Exception {
		when(veterinarioService.listarTodos()).thenReturn(List.of(new VeterinarioResponse(1L, "A", "x", true)));
		veterinariosMvc.perform(get("/api/veterinarios")).andExpect(status().isOk()).andExpect(jsonPath("$[0].nombre").value("A"));
		when(veterinarioService.listarActivos()).thenReturn(List.of());
		veterinariosMvc.perform(get("/api/veterinarios/activos")).andExpect(status().isOk());
		when(veterinarioService.obtener(2L)).thenReturn(new VeterinarioResponse(2L, "B", null, false));
		veterinariosMvc.perform(get("/api/veterinarios/2")).andExpect(status().isOk());
		when(veterinarioService.crear(any())).thenReturn(new VeterinarioResponse(3L, "C", "e", true));
		veterinariosMvc.perform(post("/api/veterinarios").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"C\",\"especialidad\":\"e\",\"activo\":true}"))
				.andExpect(status().isCreated());
		when(veterinarioService.actualizar(anyLong(), any())).thenReturn(new VeterinarioResponse(3L, "C2", "e", true));
		veterinariosMvc.perform(put("/api/veterinarios/3").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"C2\",\"especialidad\":\"e\",\"activo\":true}"))
				.andExpect(status().isOk());
		veterinariosMvc.perform(delete("/api/veterinarios/3")).andExpect(status().isNoContent());
	}

	@Test
	void duenosEndpoints() throws Exception {
		when(duenoService.listar()).thenReturn(List.of(new DuenoResponse(1L, "N", null, null, null)));
		duenosMvc.perform(get("/api/duenos")).andExpect(status().isOk());
		when(duenoService.obtener(1L)).thenReturn(new DuenoResponse(1L, "N", "e", "t", "d"));
		duenosMvc.perform(get("/api/duenos/1")).andExpect(status().isOk());
		when(duenoService.crear(any())).thenReturn(new DuenoResponse(2L, "X", null, null, null));
		duenosMvc.perform(post("/api/duenos").contentType(MediaType.APPLICATION_JSON).content("{\"nombreCompleto\":\"X\"}"))
				.andExpect(status().isCreated());
		when(duenoService.actualizar(anyLong(), any())).thenReturn(new DuenoResponse(2L, "Y", null, null, null));
		duenosMvc.perform(put("/api/duenos/2").contentType(MediaType.APPLICATION_JSON).content("{\"nombreCompleto\":\"Y\"}"))
				.andExpect(status().isOk());
		duenosMvc.perform(delete("/api/duenos/2")).andExpect(status().isNoContent());
	}

	@Test
	void citasConAutenticacion() throws Exception {
		var auth = new UsernamePasswordAuthenticationToken("maria", null, List.of(new SimpleGrantedAuthority("ROLE_COORDINADOR")));
		SecurityContextHolder.getContext().setAuthentication(auth);
		when(citaService.listar(any())).thenReturn(List.of(new CitaResponse(1L, 1L, "a", 2L, "v", LocalDateTime.now(), "m", EstadoCita.PROGRAMADA, false)));
		citasMvc.perform(get("/api/citas")).andExpect(status().isOk());
		when(citaService.obtener(anyLong(), any())).thenReturn(new CitaResponse(5L, 1L, "a", 2L, "v", LocalDateTime.now(), "m", EstadoCita.PROGRAMADA, true));
		citasMvc.perform(get("/api/citas/5")).andExpect(status().isOk());
		citasMvc.perform(delete("/api/citas/5")).andExpect(status().isNoContent());
		when(citaService.crear(any(), any())).thenReturn(new CitaResponse(6L, 1L, "a", 2L, "v", LocalDateTime.now(), "m", EstadoCita.PROGRAMADA, false));
		citasMvc.perform(post("/api/citas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"animalId\":1,\"veterinarioId\":2,\"fechaHora\":\"2026-06-01T10:00:00\",\"motivo\":\"rev\"}"))
				.andExpect(status().isCreated());
	}

	@Test
	void registrosMedicos() throws Exception {
		when(registroMedicoService.listar()).thenReturn(List.of(new RegistroMedicoResponse(1L, 2L, LocalDateTime.now(), "d", null, null, null, null)));
		registrosMvc.perform(get("/api/registros-medicos")).andExpect(status().isOk());
		when(registroMedicoService.obtener(3L)).thenReturn(new RegistroMedicoResponse(3L, 2L, LocalDateTime.now(), "x", null, null, null, null));
		registrosMvc.perform(get("/api/registros-medicos/3")).andExpect(status().isOk());
		when(registroMedicoService.crear(any())).thenReturn(new RegistroMedicoResponse(4L, 2L, LocalDateTime.now(), "d", null, null, null, null));
		registrosMvc.perform(post("/api/registros-medicos")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"citaId\":1,\"fechaAtencion\":\"2026-05-01T10:00:00\",\"diagnostico\":\"dx\"}"))
				.andExpect(status().isCreated());
	}

	@Test
	void facturasEndpoints() throws Exception {
		when(facturaService.listar()).thenReturn(List.of());
		facturasMvc.perform(get("/api/facturas")).andExpect(status().isOk());
		when(facturaService.obtener(1L)).thenReturn(new FacturaResponse(1L, "F", null, BigDecimal.ONE, 1L, 1L, "a", List.of()));
		facturasMvc.perform(get("/api/facturas/1")).andExpect(status().isOk());
		when(facturaService.crear(any())).thenReturn(new FacturaResponse(2L, "F2", null, BigDecimal.TEN, 1L, 1L, "a", List.of()));
		facturasMvc.perform(post("/api/facturas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"registroMedicoId\":1,\"lineas\":[{\"tipo\":\"SERVICIO\",\"descripcion\":\"x\",\"monto\":10}]}"))
				.andExpect(status().isCreated());
		facturasMvc.perform(post("/api/facturas/2/enviar-correo")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\":\"a@b.cl\"}"))
				.andExpect(status().isNoContent());
	}
}
