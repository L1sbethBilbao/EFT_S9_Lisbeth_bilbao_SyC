package com.duoc.backendS8.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duoc.backendS8.entity.Veterinario;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

	List<Veterinario> findByActivoTrueOrderByNombreAsc();
}
