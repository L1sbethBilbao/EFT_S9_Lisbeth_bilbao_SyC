package com.duoc.backendS8.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.duoc.backendS8.entity.Animal;
import com.duoc.backendS8.entity.EstadoAdopcion;
import com.duoc.backendS8.entity.GeneroAnimal;

@SpringBootTest
@Transactional
class AnimalSpecificationsSpringBootTest {

	@Autowired
	private AnimalRepository animalRepository;

	@Test
	void filtraPorVariosCriterios() {
		final String esp1 = "CapybaraTestuid811";
		final String esp2 = "QuokkaTestuid811";
		animalRepository.save(Animal.builder().nombre("n1").edad(3).especie(esp1).raza("RazaPersa811").ubicacion("UbicSantiago811").genero(GeneroAnimal.FEMENINO).estadoAdopcion(EstadoAdopcion.DISPONIBLE).build());
		animalRepository.save(Animal.builder().nombre("n2").edad(8).especie(esp2).raza("RazaCaniche811").ubicacion("UbicValpo811").genero(GeneroAnimal.MASCULINO).estadoAdopcion(EstadoAdopcion.DISPONIBLE).build());

		var spec1 = AnimalSpecifications.conFiltros("capybaratestuid811", null, null, null, null, null, null);
		assertThat(animalRepository.findAll(spec1)).hasSize(1);

		var spec2 = AnimalSpecifications.conFiltros(null, "caniche811", null, null, null, null, null);
		assertThat(animalRepository.findAll(spec2)).hasSize(1);

		var spec3 = AnimalSpecifications.conFiltros(null, null, "ubicvalpo811", null, null, null, null);
		assertThat(animalRepository.findAll(spec3)).hasSize(1);

		var spec4 = AnimalSpecifications.conFiltros(null, null, null, 7, 9, null, null);
		assertThat(animalRepository.findAll(spec4).stream().filter(a -> esp1.equals(a.getEspecie()) || esp2.equals(a.getEspecie()))).hasSize(1);

		var spec5 = AnimalSpecifications.conFiltros(null, null, null, null, null, GeneroAnimal.MASCULINO, null);
		assertThat(animalRepository.findAll(spec5).stream().filter(a -> esp2.equals(a.getEspecie()))).hasSize(1);

		var spec6 = AnimalSpecifications.conFiltros(null, null, null, null, null, null, EstadoAdopcion.DISPONIBLE);
		assertThat(animalRepository.findAll(spec6).stream().filter(a -> esp1.equals(a.getEspecie()) || esp2.equals(a.getEspecie()))).hasSize(2);
	}
}
