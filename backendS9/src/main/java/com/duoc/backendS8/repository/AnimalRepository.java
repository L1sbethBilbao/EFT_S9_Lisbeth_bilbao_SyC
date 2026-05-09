package com.duoc.backendS8.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.duoc.backendS8.entity.Animal;

public interface AnimalRepository extends JpaRepository<Animal, Long>, JpaSpecificationExecutor<Animal> {

	boolean existsByNombreIgnoreCase(String nombre);
}
