package com.duoc.backendS8.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duoc.backendS8.entity.RegistroMedico;

public interface RegistroMedicoRepository extends JpaRepository<RegistroMedico, Long> {

	Optional<RegistroMedico> findByCita_Id(Long citaId);

	long countByCita_Animal_Id(Long animalId);
}
