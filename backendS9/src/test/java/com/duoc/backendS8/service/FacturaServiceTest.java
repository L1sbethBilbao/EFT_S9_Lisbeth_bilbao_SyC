package com.duoc.backendS8.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;

import com.duoc.backendS8.dto.FacturaRequest;
import com.duoc.backendS8.dto.LineaFacturaRequest;
import com.duoc.backendS8.entity.Animal;
import com.duoc.backendS8.entity.Cita;
import com.duoc.backendS8.entity.EstadoCita;
import com.duoc.backendS8.entity.Factura;
import com.duoc.backendS8.entity.RegistroMedico;
import com.duoc.backendS8.entity.TipoLineaFactura;
import com.duoc.backendS8.entity.Veterinario;
import com.duoc.backendS8.repository.FacturaRepository;
import com.duoc.backendS8.repository.RegistroMedicoRepository;

@ExtendWith(MockitoExtension.class)
class FacturaServiceTest {

	@Mock
	private FacturaRepository facturaRepository;
	@Mock
	private RegistroMedicoRepository registroMedicoRepository;
	@Mock
	private FacturaNotificacionService facturaNotificacionService;

	@InjectMocks
	private FacturaService facturaService;

	@Test
	void crearSumaLineasYAsociaRegistro() {
		Animal animal = Animal.builder().nombre("Firulais").build();
		Veterinario vet = Veterinario.builder().id(1L).nombre("Dr").activo(true).build();
		Cita cita = Cita.builder()
				.id(7L)
				.animal(animal)
				.veterinario(vet)
				.fechaHora(LocalDateTime.now())
				.estado(EstadoCita.COMPLETADA)
				.build();
		RegistroMedico rm = RegistroMedico.builder().id(20L).factura(null).cita(cita).build();
		when(registroMedicoRepository.findById(20L)).thenReturn(Optional.of(rm));
		when(facturaRepository.count()).thenReturn(0L);
		doAnswer(inv -> {
			Factura f = inv.getArgument(0);
			f.setId(99L);
			return f;
		}).when(facturaRepository).save(any(Factura.class));

		LineaFacturaRequest linea = new LineaFacturaRequest(TipoLineaFactura.SERVICIO, "  Visita  ", new BigDecimal("15000.00"));
		var res = facturaService.crear(new FacturaRequest(20L, List.of(linea)));

		assertThat(res.id()).isEqualTo(99L);
		assertThat(res.total()).isEqualByComparingTo("15000.00");
		assertThat(rm.getFactura()).isNotNull();
	}

	@Test
	void obtenerNotFound() {
		when(facturaRepository.findById(1L)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> facturaService.obtener(1L)).isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void enviarCorreoDelega() {
		Factura f = Factura.builder().id(1L).build();
		when(facturaRepository.findById(1L)).thenReturn(Optional.of(f));
		facturaService.enviarCorreo(1L, "a@b.cl");
		verify(facturaNotificacionService).enviarPorCorreo(f, "a@b.cl");
	}
}
