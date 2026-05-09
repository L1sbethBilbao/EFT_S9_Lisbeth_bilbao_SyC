package com.duoc.backendS8.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
class CitaServiceTest {

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

	@Test
	void listarCoordinadorUsaTodasLasCitas() {
		Usuario maria = Usuario.builder().username("maria").rol(RolUsuario.COORDINADOR).build();
		when(usuarioRepository.findByUsernameFetchingVeterinario("maria")).thenReturn(Optional.of(maria));
		Animal animal = Animal.builder().id(1L).nombre("Rayo").build();
		Veterinario vet = Veterinario.builder().id(2L).nombre("Dr").build();
		Cita cita = Cita.builder().id(10L).animal(animal).veterinario(vet).fechaHora(LocalDateTime.now()).estado(EstadoCita.PROGRAMADA).build();
		when(citaRepository.findAllByOrderByFechaHoraDesc()).thenReturn(List.of(cita));
		when(registroMedicoRepository.countByCita_Animal_Id(1L)).thenReturn(0L);

		var auth = new UsernamePasswordAuthenticationToken(
				"maria",
				null,
				List.of(new SimpleGrantedAuthority("ROLE_COORDINADOR")));

		assertThat(citaService.listar(auth)).hasSize(1);
		assertThat(citaService.listar(auth).get(0).animalNombre()).isEqualTo("Rayo");
	}

	@Test
	void listarVeterinarioSinPerfilDevuelveVacio() {
		Usuario pedro = Usuario.builder().username("pedro").rol(RolUsuario.VETERINARIO).veterinario(null).build();
		when(usuarioRepository.findByUsernameFetchingVeterinario("pedro")).thenReturn(Optional.of(pedro));
		var auth = new UsernamePasswordAuthenticationToken("pedro", null, List.of(new SimpleGrantedAuthority("ROLE_VETERINARIO")));
		assertThat(citaService.listar(auth)).isEmpty();
	}
}
