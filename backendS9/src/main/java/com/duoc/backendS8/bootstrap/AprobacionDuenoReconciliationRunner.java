package com.duoc.backendS8.bootstrap;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.duoc.backendS8.service.SolicitudAdopcionService;

/**
 * Vincula dueños y animales para solicitudes ya aprobadas que quedaron sin migrar
 * (datos anteriores a la lógica de negocio). Idempotente en cada arranque.
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
class AprobacionDuenoReconciliationRunner implements ApplicationRunner {

	private final SolicitudAdopcionService solicitudAdopcionService;

	AprobacionDuenoReconciliationRunner(SolicitudAdopcionService solicitudAdopcionService) {
		this.solicitudAdopcionService = solicitudAdopcionService;
	}

	@Override
	public void run(ApplicationArguments args) {
		solicitudAdopcionService.reconciliarAprobadasSinDueñoEnAnimal();
	}
}
