package com.duoc.frontendS8.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.duoc.frontendS8.dto.FacturaForm;
import com.duoc.frontendS8.dto.LineaFacturaForm;
import com.duoc.frontendS8.dto.RegistroMedicoDto;
import com.duoc.frontendS8.service.BackendApiClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/facturas")
public class FacturaWebController {

	private static final BigDecimal MONTO_CONSULTA_SUGERIDO = new BigDecimal("45000");
	private static final BigDecimal MONTO_MEDICAMENTO_SUGERIDO = new BigDecimal("12000");

	private final BackendApiClient backendApiClient;

	public FacturaWebController(BackendApiClient backendApiClient) {
		this.backendApiClient = backendApiClient;
	}

	@GetMapping
	public String listar(Model model, HttpServletRequest request) {
		model.addAttribute("facturas", backendApiClient.listarFacturas(SessionJwt.get(request)));
		return "facturas";
	}

	@GetMapping("/{id}")
	public String detalle(@PathVariable Long id, Model model, HttpServletRequest request) {
		model.addAttribute("factura", backendApiClient.obtenerFactura(SessionJwt.get(request), id));
		return "factura-detalle";
	}

	@GetMapping("/nueva")
	public String nueva(@RequestParam Long registroMedicoId, Model model, HttpServletRequest request) {
		RegistroMedicoDto rm = backendApiClient.obtenerRegistroMedico(SessionJwt.get(request), registroMedicoId);
		List<LineaFacturaForm> lineas = lineasPrefill(rm);
		model.addAttribute("facturaForm", new FacturaForm(registroMedicoId, lineas));
		return "factura-form";
	}

	private static List<LineaFacturaForm> lineasPrefill(RegistroMedicoDto rm) {
		List<LineaFacturaForm> lineas = new ArrayList<>();
		if (rm == null) {
			lineas.add(new LineaFacturaForm("SERVICIO", "Consulta veterinaria", MONTO_CONSULTA_SUGERIDO));
			lineas.add(new LineaFacturaForm("MEDICAMENTO", "Medicamentos / insumos", MONTO_MEDICAMENTO_SUGERIDO));
			return lineas;
		}
		String descServicio = "Consulta veterinaria";
		if (rm.diagnostico() != null && !rm.diagnostico().isBlank()) {
			descServicio = descServicio + " — " + truncar(rm.diagnostico().trim(), 200);
		}
		lineas.add(new LineaFacturaForm("SERVICIO", descServicio, MONTO_CONSULTA_SUGERIDO));
		String meds = rm.medicamentos();
		boolean hayMedicamentos = meds != null && !meds.isBlank();
		String descMed = hayMedicamentos ? truncar(meds.trim(), 200) : "Sin medicamentos en esta visita";
		BigDecimal montoMed = hayMedicamentos ? MONTO_MEDICAMENTO_SUGERIDO : BigDecimal.ZERO;
		lineas.add(new LineaFacturaForm("MEDICAMENTO", descMed, montoMed));
		return lineas;
	}

	private static String truncar(String s, int max) {
		if (s.length() <= max) {
			return s;
		}
		return s.substring(0, max - 1) + "…";
	}

	@PostMapping
	public String crear(
			@Valid @ModelAttribute("facturaForm") FacturaForm form,
			BindingResult bindingResult,
			Model model,
			HttpServletRequest request,
			RedirectAttributes ra) {
		if (bindingResult.hasErrors()) {
			return "factura-form";
		}
		backendApiClient.crearFactura(SessionJwt.get(request), form);
		ra.addFlashAttribute("mensaje", "Factura emitida.");
		return "redirect:/facturas";
	}

	@PostMapping("/{id}/enviar-correo")
	public String enviarCorreo(
			@PathVariable Long id,
			@RequestParam String email,
			HttpServletRequest request,
			RedirectAttributes ra) {
		backendApiClient.enviarFacturaCorreo(SessionJwt.get(request), id, email);
		ra.addFlashAttribute("mensaje", "Solicitud de envío por correo registrada (revisa logs del servidor en demo).");
		return "redirect:/facturas/" + id;
	}
}
