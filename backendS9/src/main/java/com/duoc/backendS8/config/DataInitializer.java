package com.duoc.backendS8.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.duoc.backendS8.entity.Animal;
import com.duoc.backendS8.entity.Cita;
import com.duoc.backendS8.entity.Dueno;
import com.duoc.backendS8.entity.EstadoAdopcion;
import com.duoc.backendS8.entity.EstadoCita;
import com.duoc.backendS8.entity.Factura;
import com.duoc.backendS8.entity.GeneroAnimal;
import com.duoc.backendS8.entity.LineaFactura;
import com.duoc.backendS8.entity.RegistroMedico;
import com.duoc.backendS8.entity.TipoLineaFactura;
import com.duoc.backendS8.entity.RolUsuario;
import com.duoc.backendS8.entity.Usuario;
import com.duoc.backendS8.entity.Veterinario;
import com.duoc.backendS8.repository.AnimalRepository;
import com.duoc.backendS8.repository.CitaRepository;
import com.duoc.backendS8.repository.DuenoRepository;
import com.duoc.backendS8.repository.FacturaRepository;
import com.duoc.backendS8.repository.RegistroMedicoRepository;
import com.duoc.backendS8.repository.UsuarioRepository;
import com.duoc.backendS8.repository.VeterinarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

	private final UsuarioRepository usuarioRepository;
	private final AnimalRepository animalRepository;
	private final DuenoRepository duenoRepository;
	private final VeterinarioRepository veterinarioRepository;
	private final CitaRepository citaRepository;
	private final RegistroMedicoRepository registroMedicoRepository;
	private final FacturaRepository facturaRepository;
	private final PasswordEncoder passwordEncoder;
	private final String seedUserPassword;

	public DataInitializer(
			UsuarioRepository usuarioRepository,
			AnimalRepository animalRepository,
			DuenoRepository duenoRepository,
			VeterinarioRepository veterinarioRepository,
			CitaRepository citaRepository,
			RegistroMedicoRepository registroMedicoRepository,
			FacturaRepository facturaRepository,
			PasswordEncoder passwordEncoder,
			@Value("${app.seed.user-password}") String seedUserPassword) {
		this.usuarioRepository = usuarioRepository;
		this.animalRepository = animalRepository;
		this.duenoRepository = duenoRepository;
		this.veterinarioRepository = veterinarioRepository;
		this.citaRepository = citaRepository;
		this.registroMedicoRepository = registroMedicoRepository;
		this.facturaRepository = facturaRepository;
		this.passwordEncoder = passwordEncoder;
		this.seedUserPassword = seedUserPassword;
	}

	@Override
	@Transactional
	public void run(String... args) {
		if (usuarioRepository.count() == 0) {
			usuarioRepository.save(Usuario.builder()
					.username("maria")
					.password(passwordEncoder.encode(seedUserPassword))
					.rol(RolUsuario.COORDINADOR)
					.build());
			usuarioRepository.save(Usuario.builder()
					.username("pedro")
					.password(passwordEncoder.encode(seedUserPassword))
					.rol(RolUsuario.VETERINARIO)
					.build());
			usuarioRepository.save(Usuario.builder()
					.username("luis")
					.password(passwordEncoder.encode(seedUserPassword))
					.rol(RolUsuario.GESTOR)
					.build());
		}

		if (duenoRepository.count() > 0) {
			vincularPedroConVeterinario();
			seedMascotasCatalogoAmpliado();
			return;
		}

		Dueno duenoAna = duenoRepository.save(Dueno.builder()
				.nombreCompleto("Ana Pérez")
				.email("ana.perez@ejemplo.cl")
				.telefono("+56912345678")
				.direccion("Santiago Centro")
				.build());
		Dueno duenoCarlos = duenoRepository.save(Dueno.builder()
				.nombreCompleto("Carlos Rojas")
				.email("carlos@ejemplo.cl")
				.telefono("+56987654321")
				.direccion("Providencia")
				.build());

		Veterinario vet1 = veterinarioRepository.save(Veterinario.builder()
				.nombre("Dra. Marta Soto")
				.especialidad("Medicina general")
				.activo(true)
				.build());
		Veterinario vet2 = veterinarioRepository.save(Veterinario.builder()
				.nombre("Dr. Pedro Morales")
				.especialidad("Cirugía menor")
				.activo(true)
				.build());

		Animal luna = animalRepository.save(Animal.builder()
				.nombre("Luna")
				.especie("Perro")
				.raza("Mestizo")
				.edad(3)
				.ubicacion("Santiago")
				.genero(GeneroAnimal.FEMENINO)
				.estadoAdopcion(EstadoAdopcion.DISPONIBLE)
				.fotoUrl("https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=400")
				.dueno(null)
				.build());
		Animal mish = animalRepository.save(Animal.builder()
				.nombre("Mish")
				.especie("Gato")
				.raza("Siamés")
				.edad(2)
				.ubicacion("Providencia")
				.genero(GeneroAnimal.MASCULINO)
				.estadoAdopcion(EstadoAdopcion.EN_ACOGIDA)
				.fotoUrl("https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=400")
				.dueno(duenoAna)
				.build());
		animalRepository.save(Animal.builder()
				.nombre("Kiwi")
				.especie("Loro")
				.raza("Amazonas")
				.edad(5)
				.ubicacion("Maipú")
				.genero(GeneroAnimal.DESCONOCIDO)
				.estadoAdopcion(EstadoAdopcion.ADOPTADO)
				.fotoUrl(null)
				.dueno(duenoCarlos)
				.build());

		Cita citaDemo = citaRepository.save(Cita.builder()
				.animal(luna)
				.veterinario(vet1)
				.fechaHora(LocalDateTime.now().minusDays(1).withHour(10).withMinute(0))
				.motivo("Control anual y vacuna")
				.estado(EstadoCita.PROGRAMADA)
				.build());

		RegistroMedico registro = registroMedicoRepository.save(RegistroMedico.builder()
				.cita(citaDemo)
				.fechaAtencion(LocalDateTime.now().minusDays(1).withHour(11).withMinute(0))
				.diagnostico("Estado general bueno; se aplicó vacuna anual.")
				.tratamiento("Reposo 24 h")
				.medicamentos("Antiinflamatorio suave")
				.notas("Próxima revisión en 6 meses.")
				.build());
		citaDemo.setEstado(EstadoCita.COMPLETADA);
		citaRepository.save(citaDemo);

		Factura factura = Factura.builder()
				.registroMedico(registro)
				.numeroFactura("F-" + LocalDate.now().getYear() + "-00001")
				.fechaEmision(LocalDate.now())
				.total(BigDecimal.ZERO)
				.build();
		LineaFactura l1 = LineaFactura.builder()
				.factura(factura)
				.tipo(TipoLineaFactura.SERVICIO)
				.descripcion("Consulta veterinaria y vacunación")
				.monto(new BigDecimal("45000"))
				.build();
		LineaFactura l2 = LineaFactura.builder()
				.factura(factura)
				.tipo(TipoLineaFactura.MEDICAMENTO)
				.descripcion("Medicamento antiinflamatorio")
				.monto(new BigDecimal("12000"))
				.build();
		factura.getLineas().add(l1);
		factura.getLineas().add(l2);
		factura.setTotal(new BigDecimal("57000"));
		facturaRepository.save(factura);
		registro.setFactura(factura);

		citaRepository.save(Cita.builder()
				.animal(mish)
				.veterinario(vet2)
				.fechaHora(LocalDateTime.now().plusDays(3).withHour(15).withMinute(0))
				.motivo("Revisión dermatológica")
				.estado(EstadoCita.PROGRAMADA)
				.build());

		vincularPedroConVeterinario();
		seedMascotasCatalogoAmpliado();
	}

	/**
	 * Añade mascotas de demostración si aún no existen (por nombre), para enriquecer el catálogo
	 * sin borrar datos en bases ya inicializadas.
	 */
	private void seedMascotasCatalogoAmpliado() {
		Dueno duenoAna = duenoRepository.findAll().stream()
				.filter(d -> d.getNombreCompleto() != null
						&& d.getNombreCompleto().toLowerCase().contains("ana"))
				.findFirst()
				.orElse(null);
		Dueno duenoCarlos = duenoRepository.findAll().stream()
				.filter(d -> d.getNombreCompleto() != null
						&& d.getNombreCompleto().toLowerCase().contains("carlos"))
				.findFirst()
				.orElse(null);

		List<Animal> extras = Arrays.asList(
				Animal.builder().nombre("Toby").especie("Perro").raza("Labrador").edad(4).ubicacion("Viña del Mar")
						.genero(GeneroAnimal.MASCULINO).estadoAdopcion(EstadoAdopcion.DISPONIBLE)
						.fotoUrl("https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=400").dueno(null).build(),
				Animal.builder().nombre("Nala").especie("Gato").raza("Persa").edad(1).ubicacion("Valparaíso")
						.genero(GeneroAnimal.FEMENINO).estadoAdopcion(EstadoAdopcion.DISPONIBLE)
						.fotoUrl("https://images.unsplash.com/photo-1574158622682-e40e69881006?w=400").dueno(null).build(),
				Animal.builder().nombre("Simba").especie("Perro").raza("Beagle").edad(2).ubicacion("Concepción")
						.genero(GeneroAnimal.MASCULINO).estadoAdopcion(EstadoAdopcion.EN_ACOGIDA)
						.fotoUrl("https://images.unsplash.com/photo-1598133894008-61f7fdb8cc3a?w=400").dueno(duenoAna).build(),
				Animal.builder().nombre("Canela").especie("Conejo").raza("Enano").edad(1).ubicacion("La Serena")
						.genero(GeneroAnimal.FEMENINO).estadoAdopcion(EstadoAdopcion.DISPONIBLE)
						.fotoUrl("https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?w=400").dueno(null).build(),
				Animal.builder().nombre("Thor").especie("Perro").raza("Pastor alemán").edad(5).ubicacion("Antofagasta")
						.genero(GeneroAnimal.MASCULINO).estadoAdopcion(EstadoAdopcion.DISPONIBLE)
						.fotoUrl("https://images.unsplash.com/photo-1568572933382-74d440642377?w=400").dueno(null).build(),
				Animal.builder().nombre("Mimi").especie("Gato").raza("Mestizo").edad(6).ubicacion("Temuco")
						.genero(GeneroAnimal.FEMENINO).estadoAdopcion(EstadoAdopcion.ADOPTADO)
						.fotoUrl("https://images.unsplash.com/photo-1494256997604-768d1f608cac?w=400").dueno(duenoCarlos).build(),
				Animal.builder().nombre("Zeus").especie("Perro").raza("Bulldog francés").edad(3).ubicacion("Puerto Montt")
						.genero(GeneroAnimal.MASCULINO).estadoAdopcion(EstadoAdopcion.EN_ACOGIDA)
						.fotoUrl("https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=400").dueno(null).build(),
				Animal.builder().nombre("Lola").especie("Gato").raza("Mestizo").edad(2).ubicacion("Rancagua")
						.genero(GeneroAnimal.FEMENINO).estadoAdopcion(EstadoAdopcion.DISPONIBLE)
						.fotoUrl("https://images.unsplash.com/photo-1533738363-b7f9aef128ce?w=400").dueno(null).build(),
				Animal.builder().nombre("Coco").especie("Loro").raza("Cacatúa").edad(8).ubicacion("Santiago")
						.genero(GeneroAnimal.DESCONOCIDO).estadoAdopcion(EstadoAdopcion.NO_APLICA_CLINICA)
						.fotoUrl("https://images.unsplash.com/photo-1552728089-57bdde30beb7?w=400").dueno(duenoAna).build(),
				Animal.builder().nombre("Max").especie("Perro").raza("Golden retriever").edad(4).ubicacion("Ñuñoa")
						.genero(GeneroAnimal.MASCULINO).estadoAdopcion(EstadoAdopcion.DISPONIBLE)
						.fotoUrl("https://images.unsplash.com/photo-1633722715463-d30f42f815e4?w=400").dueno(null).build(),
				Animal.builder().nombre("Bella").especie("Gato").raza("British shorthair").edad(3).ubicacion("Las Condes")
						.genero(GeneroAnimal.FEMENINO).estadoAdopcion(EstadoAdopcion.DISPONIBLE)
						.fotoUrl("https://images.unsplash.com/photo-1529774898137-bff8a61bd5ca?w=400").dueno(null).build(),
				Animal.builder().nombre("Duke").especie("Perro").raza("Rottweiler").edad(6).ubicacion("Peñalolén")
						.genero(GeneroAnimal.MASCULINO).estadoAdopcion(EstadoAdopcion.ADOPTADO)
						.fotoUrl("https://images.unsplash.com/photo-1567752881298-894bb81f9379?w=400").dueno(duenoCarlos).build(),
				Animal.builder().nombre("Pepa").especie("Gato").raza("Naranjo").edad(1).ubicacion("Quilicura")
						.genero(GeneroAnimal.FEMENINO).estadoAdopcion(EstadoAdopcion.DISPONIBLE)
						.fotoUrl("https://images.unsplash.com/photo-1518791841217-8f162f1e1131?w=400").dueno(null).build(),
				Animal.builder().nombre("Alf").especie("Conejo").raza("Belier").edad(2).ubicacion("Maipú")
						.genero(GeneroAnimal.MASCULINO).estadoAdopcion(EstadoAdopcion.EN_ACOGIDA)
						.fotoUrl("https://images.unsplash.com/photo-1591382386623-e08f4d75f68d?w=400").dueno(null).build(),
				Animal.builder().nombre("Kira").especie("Perro").raza("Husky").edad(2).ubicacion("Lo Barnechea")
						.genero(GeneroAnimal.FEMENINO).estadoAdopcion(EstadoAdopcion.DISPONIBLE)
						.fotoUrl("https://images.unsplash.com/photo-1605568427561-40dd23c2acea?w=400").dueno(null).build());

		for (Animal a : extras) {
			if (!animalRepository.existsByNombreIgnoreCase(a.getNombre())) {
				animalRepository.save(a);
			}
		}
	}

	/** Vincula el login <code>pedro</code> a su fila de veterinario (demo nuevo o BD antigua con Jorge). */
	private void vincularPedroConVeterinario() {
		usuarioRepository.findByUsername("pedro").ifPresent(u -> {
			if (u.getVeterinario() != null) {
				return;
			}
			veterinarioRepository.findAll().stream()
					.filter(v -> v.getNombre() != null
							&& (v.getNombre().contains("Pedro Morales") || v.getNombre().contains("Jorge Neira")))
					.findFirst()
					.ifPresent(v -> {
						u.setVeterinario(v);
						usuarioRepository.save(u);
					});
		});
	}
}
