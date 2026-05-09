package com.duoc.backendS8.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.duoc.backendS8.entity.Animal;
import com.duoc.backendS8.entity.EstadoAdopcion;
import com.duoc.backendS8.entity.GeneroAnimal;

import jakarta.persistence.criteria.Predicate;

public final class AnimalSpecifications {

	private AnimalSpecifications() {
	}

	public static Specification<Animal> conFiltros(
			String especie,
			String raza,
			String ubicacion,
			Integer edadMin,
			Integer edadMax,
			GeneroAnimal genero,
			EstadoAdopcion estadoAdopcion) {
		return (root, query, cb) -> {
			List<Predicate> partes = new ArrayList<>();
			if (especie != null && !especie.isBlank()) {
				String like = "%" + especie.trim().toLowerCase() + "%";
				partes.add(cb.like(
						cb.lower(cb.coalesce(root.<String>get("especie"), cb.literal(""))),
						like));
			}
			if (raza != null && !raza.isBlank()) {
				String like = "%" + raza.trim().toLowerCase() + "%";
				partes.add(cb.like(
						cb.lower(cb.coalesce(root.<String>get("raza"), cb.literal(""))),
						like));
			}
			if (ubicacion != null && !ubicacion.isBlank()) {
				String like = "%" + ubicacion.trim().toLowerCase() + "%";
				partes.add(cb.like(
						cb.lower(cb.coalesce(root.<String>get("ubicacion"), cb.literal(""))),
						like));
			}
			if (edadMin != null) {
				partes.add(cb.greaterThanOrEqualTo(root.get("edad"), edadMin));
			}
			if (edadMax != null) {
				partes.add(cb.lessThanOrEqualTo(root.get("edad"), edadMax));
			}
			if (genero != null) {
				partes.add(cb.equal(root.get("genero"), genero));
			}
			if (estadoAdopcion != null) {
				partes.add(cb.equal(root.get("estadoAdopcion"), estadoAdopcion));
			}
			if (partes.isEmpty()) {
				return cb.conjunction();
			}
			return cb.and(partes.toArray(Predicate[]::new));
		};
	}
}
