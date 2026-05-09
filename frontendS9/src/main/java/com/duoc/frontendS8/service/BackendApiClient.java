package com.duoc.frontendS8.service;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.duoc.frontendS8.dto.AnimalDto;
import com.duoc.frontendS8.dto.AnimalForm;
import com.duoc.frontendS8.dto.CitaForm;
import com.duoc.frontendS8.dto.CitaDto;
import com.duoc.frontendS8.dto.EnviarFacturaDto;
import com.duoc.frontendS8.dto.DuenoDto;
import com.duoc.frontendS8.dto.DuenoForm;
import com.duoc.frontendS8.dto.FacturaDto;
import com.duoc.frontendS8.dto.FacturaForm;
import com.duoc.frontendS8.dto.LoginApiRequest;
import com.duoc.frontendS8.dto.LoginApiResponse;
import com.duoc.frontendS8.dto.RegistroMedicoDto;
import com.duoc.frontendS8.dto.RegistroMedicoForm;
import com.duoc.frontendS8.dto.SessionUsuarioDto;
import com.duoc.frontendS8.dto.SolicitudAdopcionApiRequest;
import com.duoc.frontendS8.dto.SolicitudAdopcionCreadaDto;
import com.duoc.frontendS8.dto.SolicitudAdopcionDetalleDto;
import com.duoc.frontendS8.dto.SolicitudDatosPatchApiRequest;
import com.duoc.frontendS8.dto.SolicitudAdopcionListItemDto;
import com.duoc.frontendS8.dto.MensajeCoordinadorApiRequest;
import com.duoc.frontendS8.dto.MensajeSeguimientoApiRequest;
import com.duoc.frontendS8.dto.SolicitudSeguimientoVistaDto;
import com.duoc.frontendS8.dto.EstadoSolicitudApiPatch;
import com.duoc.frontendS8.dto.VeterinarioDto;
import com.duoc.frontendS8.dto.VeterinarioForm;
import com.duoc.frontendS8.exception.BackendUnavailableException;

@Service
public class BackendApiClient {

	public static final String SESSION_JWT = "JWT_TOKEN";

	private final RestTemplate restTemplate;
	private final String baseUrl;

	public BackendApiClient(RestTemplate restTemplate, @Value("${app.backend.base-url}") String baseUrl) {
		this.restTemplate = restTemplate;
		this.baseUrl = baseUrl.replaceAll("/$", "");
	}

	public List<AnimalDto> listarAnimales(
			String especie,
			String raza,
			String ubicacion,
			Integer edadMin,
			Integer edadMax,
			String genero,
			String estadoAdopcion) {
		UriComponentsBuilder b = UriComponentsBuilder.fromUriString(baseUrl + "/api/animales");
		if (especie != null && !especie.isBlank()) {
			b.queryParam("especie", especie.trim());
		}
		if (raza != null && !raza.isBlank()) {
			b.queryParam("raza", raza.trim());
		}
		if (ubicacion != null && !ubicacion.isBlank()) {
			b.queryParam("ubicacion", ubicacion.trim());
		}
		if (edadMin != null) {
			b.queryParam("edadMin", edadMin);
		}
		if (edadMax != null) {
			b.queryParam("edadMax", edadMax);
		}
		if (genero != null && !genero.isBlank()) {
			b.queryParam("genero", genero.trim());
		}
		if (estadoAdopcion != null && !estadoAdopcion.isBlank()) {
			b.queryParam("estadoAdopcion", estadoAdopcion.trim());
		}
		URI uri = b.encode().build().toUri();
		ResponseEntity<List<AnimalDto>> response = restTemplate.exchange(
				uri,
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<AnimalDto>>() {
				});
		List<AnimalDto> body = response.getBody();
		return body != null ? body : Collections.emptyList();
	}

	public AnimalDto obtenerAnimal(Long id) {
		return restTemplate.getForObject(baseUrl + "/api/animales/" + id, AnimalDto.class);
	}

	public LoginApiResponse login(String username, String password) {
		var body = new LoginApiRequest(username, password);
		try {
			return restTemplate.postForObject(baseUrl + "/api/auth/login", body, LoginApiResponse.class);
		}
		catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
				return null;
			}
			throw e;
		}
		catch (RestClientException e) {
			throw new BackendUnavailableException(
					"No se pudo conectar con el API en " + baseUrl + ". ¿Está el backend (puerto 8080) y MySQL en ejecución?",
					e);
		}
	}

	public void crearAnimal(String jwt, AnimalForm form) {
		HttpEntity<AnimalForm> entity = new HttpEntity<>(form, jsonHeaders(jwt));
		restTemplate.postForEntity(baseUrl + "/api/animales", entity, AnimalDto.class);
	}

	public void actualizarAnimal(String jwt, Long id, AnimalForm form) {
		HttpEntity<AnimalForm> entity = new HttpEntity<>(form, jsonHeaders(jwt));
		restTemplate.exchange(baseUrl + "/api/animales/" + id, HttpMethod.PUT, entity, Void.class);
	}

	public void eliminarAnimal(String jwt, Long id) {
		HttpEntity<Void> entity = new HttpEntity<>(bearerOnly(jwt));
		ResponseEntity<Void> response = restTemplate.exchange(
				baseUrl + "/api/animales/" + id,
				HttpMethod.DELETE,
				entity,
				Void.class);
		if (!response.getStatusCode().is2xxSuccessful()) {
			throw new IllegalStateException("No se pudo eliminar el animal");
		}
	}

	public List<DuenoDto> listarDuenos(String jwt) {
		DuenoDto[] data = restTemplate.exchange(
				baseUrl + "/api/duenos",
				HttpMethod.GET,
				new HttpEntity<>(bearerOnly(jwt)),
				DuenoDto[].class).getBody();
		if (data == null) {
			return Collections.emptyList();
		}
		return Arrays.asList(data);
	}

	public void crearDueno(String jwt, DuenoForm form) {
		restTemplate.postForEntity(baseUrl + "/api/duenos", new HttpEntity<>(form, jsonHeaders(jwt)), DuenoDto.class);
	}

	public void actualizarDueno(String jwt, Long id, DuenoForm form) {
		restTemplate.exchange(baseUrl + "/api/duenos/" + id, HttpMethod.PUT, new HttpEntity<>(form, jsonHeaders(jwt)), Void.class);
	}

	public void eliminarDueno(String jwt, Long id) {
		restTemplate.exchange(baseUrl + "/api/duenos/" + id, HttpMethod.DELETE, new HttpEntity<>(bearerOnly(jwt)), Void.class);
	}

	public List<VeterinarioDto> listarVeterinarios(String jwt) {
		VeterinarioDto[] data = restTemplate.exchange(
				baseUrl + "/api/veterinarios",
				HttpMethod.GET,
				new HttpEntity<>(bearerOnly(jwt)),
				VeterinarioDto[].class).getBody();
		if (data == null) {
			return Collections.emptyList();
		}
		return Arrays.asList(data);
	}

	public List<VeterinarioDto> listarVeterinariosActivos(String jwt) {
		VeterinarioDto[] data = restTemplate.exchange(
				baseUrl + "/api/veterinarios/activos",
				HttpMethod.GET,
				new HttpEntity<>(bearerOnly(jwt)),
				VeterinarioDto[].class).getBody();
		if (data == null) {
			return Collections.emptyList();
		}
		return Arrays.asList(data);
	}

	public void crearVeterinario(String jwt, VeterinarioForm form) {
		restTemplate.postForEntity(baseUrl + "/api/veterinarios", new HttpEntity<>(form, jsonHeaders(jwt)), VeterinarioDto.class);
	}

	public void actualizarVeterinario(String jwt, Long id, VeterinarioForm form) {
		restTemplate.exchange(
				baseUrl + "/api/veterinarios/" + id,
				HttpMethod.PUT,
				new HttpEntity<>(form, jsonHeaders(jwt)),
				Void.class);
	}

	public void eliminarVeterinario(String jwt, Long id) {
		restTemplate.exchange(
				baseUrl + "/api/veterinarios/" + id,
				HttpMethod.DELETE,
				new HttpEntity<>(bearerOnly(jwt)),
				Void.class);
	}

	public SessionUsuarioDto obtenerSesion(String jwt) {
		return restTemplate.exchange(
				baseUrl + "/api/auth/me",
				HttpMethod.GET,
				new HttpEntity<>(bearerOnly(jwt)),
				SessionUsuarioDto.class).getBody();
	}

	public List<CitaDto> listarCitas(String jwt) {
		CitaDto[] data = restTemplate.exchange(
				baseUrl + "/api/citas",
				HttpMethod.GET,
				new HttpEntity<>(bearerOnly(jwt)),
				CitaDto[].class).getBody();
		if (data == null) {
			return Collections.emptyList();
		}
		return Arrays.asList(data);
	}

	public CitaDto obtenerCita(String jwt, Long id) {
		return restTemplate.exchange(
				baseUrl + "/api/citas/" + id,
				HttpMethod.GET,
				new HttpEntity<>(bearerOnly(jwt)),
				CitaDto.class).getBody();
	}

	public void crearCita(String jwt, CitaForm form) {
		restTemplate.postForEntity(baseUrl + "/api/citas", new HttpEntity<>(form, jsonHeaders(jwt)), CitaDto.class);
	}

	public void cancelarCita(String jwt, Long id) {
		restTemplate.exchange(baseUrl + "/api/citas/" + id, HttpMethod.DELETE, new HttpEntity<>(bearerOnly(jwt)), Void.class);
	}

	public List<RegistroMedicoDto> listarRegistrosMedicos(String jwt) {
		RegistroMedicoDto[] data = restTemplate.exchange(
				baseUrl + "/api/registros-medicos",
				HttpMethod.GET,
				new HttpEntity<>(bearerOnly(jwt)),
				RegistroMedicoDto[].class).getBody();
		if (data == null) {
			return Collections.emptyList();
		}
		return Arrays.asList(data);
	}

	public void crearRegistroMedico(String jwt, RegistroMedicoForm form) {
		restTemplate.postForEntity(
				baseUrl + "/api/registros-medicos",
				new HttpEntity<>(form, jsonHeaders(jwt)),
				RegistroMedicoDto.class);
	}

	/** Obtiene un registro por id; devuelve null si no existe o falla la llamada. */
	public RegistroMedicoDto obtenerRegistroMedico(String jwt, Long id) {
		try {
			return restTemplate.exchange(
					baseUrl + "/api/registros-medicos/" + id,
					HttpMethod.GET,
					new HttpEntity<>(bearerOnly(jwt)),
					RegistroMedicoDto.class).getBody();
		} catch (HttpClientErrorException.NotFound e) {
			return null;
		} catch (RestClientException e) {
			return null;
		}
	}

	public List<FacturaDto> listarFacturas(String jwt) {
		FacturaDto[] data = restTemplate.exchange(
				baseUrl + "/api/facturas",
				HttpMethod.GET,
				new HttpEntity<>(bearerOnly(jwt)),
				FacturaDto[].class).getBody();
		if (data == null) {
			return Collections.emptyList();
		}
		return Arrays.asList(data);
	}

	public FacturaDto obtenerFactura(String jwt, Long id) {
		return restTemplate.exchange(
				baseUrl + "/api/facturas/" + id,
				HttpMethod.GET,
				new HttpEntity<>(bearerOnly(jwt)),
				FacturaDto.class).getBody();
	}

	public void crearFactura(String jwt, FacturaForm form) {
		restTemplate.postForEntity(baseUrl + "/api/facturas", new HttpEntity<>(form, jsonHeaders(jwt)), FacturaDto.class);
	}

	public void enviarFacturaCorreo(String jwt, Long facturaId, String email) {
		var body = new EnviarFacturaDto(email);
		restTemplate.postForEntity(
				baseUrl + "/api/facturas/" + facturaId + "/enviar-correo",
				new HttpEntity<>(body, jsonHeaders(jwt)),
				Void.class);
	}

	public SolicitudAdopcionCreadaDto crearSolicitudAdopcionPublica(SolicitudAdopcionApiRequest body) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return restTemplate.postForObject(
				baseUrl + "/api/adopciones/solicitudes",
				new HttpEntity<>(body, headers),
				SolicitudAdopcionCreadaDto.class);
	}

	public void enviarMensajeSeguimientoAdopcion(String codigoSeguimiento, String cuerpo) {
		var body = new MensajeSeguimientoApiRequest(codigoSeguimiento.trim(), cuerpo.trim());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		restTemplate.postForEntity(
				baseUrl + "/api/adopciones/seguimiento/mensajes",
				new HttpEntity<>(body, headers),
				Void.class);
	}

	/**
	 * Consulta pública por código (sin JWT). Devuelve null si el código no existe.
	 */
	public SolicitudSeguimientoVistaDto consultarSeguimientoPublico(String codigo) {
		if (codigo == null || codigo.isBlank()) {
			return null;
		}
		URI uri = UriComponentsBuilder.fromUriString(baseUrl + "/api/adopciones/seguimiento/consulta")
				.queryParam("codigo", codigo.trim())
				.encode()
				.build()
				.toUri();
		try {
			return restTemplate.getForObject(uri, SolicitudSeguimientoVistaDto.class);
		}
		catch (HttpClientErrorException.NotFound e) {
			return null;
		}
	}

	public List<SolicitudAdopcionListItemDto> listarSolicitudesAdopcion(String jwt) {
		SolicitudAdopcionListItemDto[] data = restTemplate.exchange(
				baseUrl + "/api/adopciones/solicitudes",
				HttpMethod.GET,
				new HttpEntity<>(bearerOnly(jwt)),
				SolicitudAdopcionListItemDto[].class).getBody();
		if (data == null) {
			return Collections.emptyList();
		}
		return Arrays.asList(data);
	}

	public SolicitudAdopcionDetalleDto obtenerSolicitudAdopcion(String jwt, Long id) {
		return restTemplate.exchange(
				baseUrl + "/api/adopciones/solicitudes/" + id,
				HttpMethod.GET,
				new HttpEntity<>(bearerOnly(jwt)),
				SolicitudAdopcionDetalleDto.class).getBody();
	}

	public void coordinadorEnviarMensajeAdopcion(String jwt, Long solicitudId, String cuerpo) {
		restTemplate.postForEntity(
				baseUrl + "/api/adopciones/solicitudes/" + solicitudId + "/mensajes",
				new HttpEntity<>(new MensajeCoordinadorApiRequest(cuerpo.trim()), jsonHeaders(jwt)),
				Void.class);
	}

	public void coordinadorActualizarEstadoSolicitud(String jwt, Long solicitudId, String estado) {
		restTemplate.exchange(
				baseUrl + "/api/adopciones/solicitudes/" + solicitudId + "/estado",
				HttpMethod.PATCH,
				new HttpEntity<>(new EstadoSolicitudApiPatch(estado), jsonHeaders(jwt)),
				Void.class);
	}

	public void eliminarSolicitudAdopcion(String jwt, Long id) {
		restTemplate.exchange(
				baseUrl + "/api/adopciones/solicitudes/" + id,
				HttpMethod.DELETE,
				new HttpEntity<>(bearerOnly(jwt)),
				Void.class);
	}

	public void coordinadorActualizarDatosSolicitud(String jwt, Long id, SolicitudDatosPatchApiRequest body) {
		restTemplate.exchange(
				baseUrl + "/api/adopciones/solicitudes/" + id + "/datos",
				HttpMethod.PATCH,
				new HttpEntity<>(body, jsonHeaders(jwt)),
				Void.class);
	}

	private static HttpHeaders jsonHeaders(String jwt) {
		HttpHeaders headers = bearerOnly(jwt);
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	private static HttpHeaders bearerOnly(String jwt) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(jwt);
		return headers;
	}
}
