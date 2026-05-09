package com.duoc.backendS8.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import com.duoc.backendS8.dto.FacturaRequest;
import com.duoc.backendS8.dto.FacturaResponse;
import com.duoc.backendS8.dto.LineaFacturaRequest;
import com.duoc.backendS8.dto.LineaFacturaResponse;
import com.duoc.backendS8.entity.Factura;
import com.duoc.backendS8.entity.LineaFactura;
import com.duoc.backendS8.entity.RegistroMedico;
import com.duoc.backendS8.repository.FacturaRepository;
import com.duoc.backendS8.repository.RegistroMedicoRepository;

@Service
public class FacturaService {

	private final FacturaRepository facturaRepository;
	private final RegistroMedicoRepository registroMedicoRepository;
	private final FacturaNotificacionService facturaNotificacionService;

	public FacturaService(
			FacturaRepository facturaRepository,
			RegistroMedicoRepository registroMedicoRepository,
			FacturaNotificacionService facturaNotificacionService) {
		this.facturaRepository = facturaRepository;
		this.registroMedicoRepository = registroMedicoRepository;
		this.facturaNotificacionService = facturaNotificacionService;
	}

	@Transactional(readOnly = true)
	public List<FacturaResponse> listar() {
		return facturaRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public FacturaResponse obtener(Long id) {
		return facturaRepository.findById(id).map(this::toResponse)
				.orElseThrow(() -> new EntityNotFoundException("Factura no encontrada"));
	}

	@Transactional
	public FacturaResponse crear(FacturaRequest request) {
		RegistroMedico rm = registroMedicoRepository.findById(request.registroMedicoId())
				.orElseThrow(() -> new EntityNotFoundException("Registro médico no encontrado"));
		if (rm.getFactura() != null) {
			throw new IllegalStateException("Este registro ya tiene factura emitida");
		}
		Factura factura = Factura.builder()
				.registroMedico(rm)
				.numeroFactura(generarNumero())
				.fechaEmision(LocalDate.now())
				.total(BigDecimal.ZERO)
				.build();
		BigDecimal total = BigDecimal.ZERO;
		for (LineaFacturaRequest lr : request.lineas()) {
			LineaFactura linea = LineaFactura.builder()
					.factura(factura)
					.tipo(lr.tipo())
					.descripcion(lr.descripcion().trim())
					.monto(lr.monto())
					.build();
			factura.getLineas().add(linea);
			total = total.add(lr.monto());
		}
		factura.setTotal(total);
		Factura guardada = facturaRepository.save(factura);
		rm.setFactura(guardada);
		return toResponse(guardada);
	}

	@Transactional
	public void enviarCorreo(Long facturaId, String email) {
		Factura f = facturaRepository.findById(facturaId)
				.orElseThrow(() -> new EntityNotFoundException("Factura no encontrada"));
		facturaNotificacionService.enviarPorCorreo(f, email);
	}

	private String generarNumero() {
		long n = facturaRepository.count() + 1;
		return "F-" + LocalDate.now().getYear() + "-" + String.format("%05d", n);
	}

	private FacturaResponse toResponse(Factura f) {
		List<LineaFacturaResponse> lineas = f.getLineas().stream()
				.map(l -> new LineaFacturaResponse(l.getId(), l.getTipo(), l.getDescripcion(), l.getMonto()))
				.toList();
		String animalNombre = f.getRegistroMedico().getCita().getAnimal().getNombre();
		return new FacturaResponse(
				f.getId(),
				f.getNumeroFactura(),
				f.getFechaEmision(),
				f.getTotal(),
				f.getRegistroMedico().getId(),
				f.getRegistroMedico().getCita().getId(),
				animalNombre,
				lineas);
	}
}
