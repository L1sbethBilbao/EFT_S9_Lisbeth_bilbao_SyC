package com.duoc.backendS8.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.duoc.backendS8.entity.Factura;

/**
 * Simulación de envío por correo (sin SMTP) para cumplir el requisito de "enviar factura".
 */
@Service
public class FacturaNotificacionService {

	private static final Logger log = LoggerFactory.getLogger(FacturaNotificacionService.class);

	public void enviarPorCorreo(Factura factura, String emailDestino) {
		log.info(
				"[DEMO] Factura {} enviada a {} — total {}",
				factura.getNumeroFactura(),
				emailDestino,
				factura.getTotal());
	}
}
