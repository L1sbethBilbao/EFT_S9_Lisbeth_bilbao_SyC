package com.duoc.backendS8.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.duoc.backendS8.entity.Factura;
import com.duoc.backendS8.entity.RegistroMedico;

class FacturaNotificacionServiceTest {

	@Test
	void enviarPorCorreoNoLanza() {
		FacturaNotificacionService svc = new FacturaNotificacionService();
		RegistroMedico rm = RegistroMedico.builder().id(1L).build();
		Factura f = Factura.builder()
				.numeroFactura("F-2026-00001")
				.fechaEmision(LocalDate.now())
				.total(new BigDecimal("100.00"))
				.registroMedico(rm)
				.build();
		svc.enviarPorCorreo(f, "cliente@ejemplo.cl");
	}
}
