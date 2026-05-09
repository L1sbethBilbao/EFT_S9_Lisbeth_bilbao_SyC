package com.duoc.backendS8.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.persistence.EntityNotFoundException;

import com.duoc.backendS8.dto.CitaRequest;
import com.duoc.backendS8.entity.Animal;
import com.duoc.backendS8.entity.Cita;
import com.duoc.backendS8.entity.EstadoCita;
import com.duoc.backendS8.entity.RolUsuario;
import com.duoc.backendS8.entity.Usuario;
import com.duoc.backendS8.entity.Veterinario;
import com.duoc.backendS8.repository.AnimalRepository;
import com.duoc.backendS8.repository.CitaRepository;
import com.duoc.backendS8.repository.RegistroMedicoRepository;
import com.duoc.backendS8.repository.UsuarioRepository;
import com.duoc.backendS8.repository.VeterinarioRepository;

@ExtendWith(MockitoExtension.class)
class CitaServiceExtendedTest {

	@Mock
	private CitaRepository citaRepository;
	@Mock
	private AnimalRepository animalRepository;
	@Mock
	private VeterinarioRepository veterinarioRepository;
	@Mock
	private UsuarioRepository usuarioRepository;
	@Mock
	private RegistroMedicoRepository registroMedicoRepository;

	@InjectMocks
	private CitaService citaService;

	@AfterEach
	void clearSecurity() {
		SecurityContextHolder.clearContext();
	}

	private static UsernamePasswordAuthenticationToken auth(String user, String... roles) {
		List<SimpleGrantedAuthority> auths = java.util.Arrays.stream(roles)
				.map(r -> new SimpleGrantedAuthority("ROLE_" + r))
				.toList();
		return new UsernamePasswordAuthenticationToken(user, null, auths);
	}

	@Test
	void listarVeterinarioConPerfil() {
		Veterinario vet = Veterinario.builder().id(9L).nombre("Dr").activo(true).build();
		Usuario pedro = Usuario.builder().username("pedro").rol(RolUsuario.VETERINARIO).veterinario(vet).build();
		when(usuarioRepository.findByUsernameFetchingVeterinario("pedro")).thenReturn(Optional.of(pedro));
		Cita c = buildCita(1L, 9L);
		when(citaRepository.findByVeterinario_IdOrderByFechaHoraDesc(9L)).thenReturn(List.of(c));
		when(registroMedicoRepository.countByCita_Animal_Id(100L)).thenReturn(1L);
		var a = auth("pedro", "VETERINARIO");
		assertThat(citaService.listar(a)).hasSize(1);
	}

	@Test
	void obtenerCoordinador() {
		when(usuarioRepository.findByUsernameFetchingVeterinario("maria")).thenReturn(Optional.of(coord()));
		Cita c = buildCita(3L, 1L);
		when(citaRepository.findById(3L)).thenReturn(Optional.of(c));
		when(registroMedicoRepository.countByCita_Animal_Id(100L)).thenReturn(0L);
		assertThat(citaService.obtener(3L, auth("maria", "COORDINADOR")).id()).isEqualTo(3L);
	}

	@Test
	void obtenerVeterinarioOtroVetLanza() {
		Veterinario vet = Veterinario.builder().id(2L).nombre("A").activo(true).build();
		Usuario u = Usuario.builder().username("pedro").rol(RolUsuario.VETERINARIO).veterinario(vet).build();
		when(usuarioRepository.findByUsernameFetchingVeterinario("pedro")).thenReturn(Optional.of(u));
		Cita c = buildCita(1L, 99L);
		when(citaRepository.findById(1L)).thenReturn(Optional.of(c));
		assertThatThrownBy(() -> citaService.obtener(1L, auth("pedro", "VETERINARIO"))).isInstanceOf(AccessDeniedException.class);
	}

	@Test
	void crearCoordinador() {
		when(usuarioRepository.findByUsernameFetchingVeterinario("maria")).thenReturn(Optional.of(coord()));
		Animal animal = Animal.builder().id(10L).nombre("Pet").build();
		Veterinario v = Veterinario.builder().id(3L).nombre("Dr").activo(true).build();
		when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
		when(veterinarioRepository.findById(3L)).thenReturn(Optional.of(v));
		doAnswer(inv -> {
			Cita saved = inv.getArgument(0);
			saved.setId(50L);
			return saved;
		}).when(citaRepository).save(any(Cita.class));
		CitaRequest req = new CitaRequest(10L, 3L, LocalDateTime.now(), " control ");
		var res = citaService.crear(req, auth("maria", "COORDINADOR"));
		assertThat(res.id()).isEqualTo(50L);
	}

	@Test
	void crearVeterinarioSinPerfil() {
		Usuario u = Usuario.builder().username("x").rol(RolUsuario.VETERINARIO).veterinario(null).build();
		when(usuarioRepository.findByUsernameFetchingVeterinario("x")).thenReturn(Optional.of(u));
		CitaRequest req = new CitaRequest(1L, 1L, LocalDateTime.now(), "m");
		assertThatThrownBy(() -> citaService.crear(req, auth("x", "VETERINARIO")))
				.isInstanceOf(AccessDeniedException.class);
	}

	@Test
	void crearVeterinarioFuerzaSuVet() {
		Veterinario vet = Veterinario.builder().id(7L).nombre("Dr").activo(true).build();
		Usuario u = Usuario.builder().username("pedro").rol(RolUsuario.VETERINARIO).veterinario(vet).build();
		when(usuarioRepository.findByUsernameFetchingVeterinario("pedro")).thenReturn(Optional.of(u));
		when(animalRepository.findById(1L)).thenReturn(Optional.of(Animal.builder().id(1L).nombre("A").build()));
		when(veterinarioRepository.findById(7L)).thenReturn(Optional.of(vet));
		doAnswer(inv -> {
			Cita c = inv.getArgument(0);
			c.setId(8L);
			return c;
		}).when(citaRepository).save(any(Cita.class));
		CitaRequest req = new CitaRequest(1L, 999L, LocalDateTime.now(), "m");
		var r = citaService.crear(req, auth("pedro", "VETERINARIO"));
		assertThat(r.veterinarioId()).isEqualTo(7L);
	}

	@Test
	void crearVeterinarioInactivo() {
		Veterinario vet = Veterinario.builder().id(1L).activo(false).build();
		Usuario u = Usuario.builder().username("p").rol(RolUsuario.VETERINARIO).veterinario(vet).build();
		when(usuarioRepository.findByUsernameFetchingVeterinario("p")).thenReturn(Optional.of(u));
		when(animalRepository.findById(1L)).thenReturn(Optional.of(Animal.builder().id(1L).nombre("A").build()));
		when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(vet));
		assertThatThrownBy(() -> citaService.crear(new CitaRequest(1L, 1L, LocalDateTime.now(), "m"), auth("p", "VETERINARIO")))
				.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void cancelarOk() {
		when(usuarioRepository.findByUsernameFetchingVeterinario("maria")).thenReturn(Optional.of(coord()));
		Cita c = buildCita(4L, 1L);
		c.setEstado(EstadoCita.PROGRAMADA);
		when(citaRepository.findById(4L)).thenReturn(Optional.of(c));
		citaService.cancelar(4L, auth("maria", "COORDINADOR"));
		assertThat(c.getEstado()).isEqualTo(EstadoCita.CANCELADA);
	}

	@Test
	void cancelarNoProgramada() {
		when(usuarioRepository.findByUsernameFetchingVeterinario("maria")).thenReturn(Optional.of(coord()));
		Cita c = buildCita(4L, 1L);
		c.setEstado(EstadoCita.COMPLETADA);
		when(citaRepository.findById(4L)).thenReturn(Optional.of(c));
		assertThatThrownBy(() -> citaService.cancelar(4L, auth("maria", "COORDINADOR"))).isInstanceOf(IllegalStateException.class);
	}

	private static Usuario coord() {
		return Usuario.builder().username("maria").rol(RolUsuario.COORDINADOR).build();
	}

	private static Cita buildCita(long id, long vetId) {
		Animal a = Animal.builder().id(100L).nombre("Pet").build();
		Veterinario v = Veterinario.builder().id(vetId).nombre("V").activo(true).build();
		return Cita.builder().id(id).animal(a).veterinario(v).fechaHora(LocalDateTime.now()).estado(EstadoCita.PROGRAMADA).build();
	}
}
