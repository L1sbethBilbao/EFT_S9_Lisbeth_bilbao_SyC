package com.duoc.backendS8.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duoc.backendS8.entity.Cita;

public interface CitaRepository extends JpaRepository<Cita, Long> {

	List<Cita> findAllByOrderByFechaHoraDesc();

	List<Cita> findByVeterinario_IdOrderByFechaHoraDesc(Long veterinarioId);
}
