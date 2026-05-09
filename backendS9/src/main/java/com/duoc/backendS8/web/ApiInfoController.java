package com.duoc.backendS8.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiInfoController {

	@GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
	public String raiz() {
		return """
				Backend Unidos por los Animales — API en ejecución (puerto 8080 en el servicio).

				Con Docker en tu PC la API suele estar en http://localhost:9080 (mapeo en docker-compose).
				Interfaz web (Thymeleaf): http://localhost:8081

				Con Maven en local (sin Docker): esta API en http://localhost:8080

				Endpoints públicos:
				  POST /api/auth/login     — body JSON { "username", "password" }
				  GET  /api/animales       — listado y filtros (sin token)
				  GET  /api/animales/{id}
				  POST /api/adopciones/solicitudes — formulario solicitud adopción (sin token)
				  POST /api/adopciones/seguimiento/mensajes — mensaje al coordinador con código (sin token)
				  GET  /api/adopciones/seguimiento/consulta?codigo= — estado y mensajes de la solicitud (sin token)

				Adopciones (solo COORDINADOR, JWT): GET /api/adopciones/solicitudes, GET .../solicitudes/{id},
				  POST .../solicitudes/{id}/mensajes, PATCH .../solicitudes/{id}/estado, PATCH .../solicitudes/{id}/datos,
				  DELETE .../solicitudes/{id}

				Resto de /api/** requiere JWT con roles (COORDINADOR, VETERINARIO, GESTOR).
				El login devuelve el token y la lista "roles".

				Estado del servicio: GET / (este texto). Spring Actuator no está en el classpath (no hay /actuator).
				""";
	}
}
