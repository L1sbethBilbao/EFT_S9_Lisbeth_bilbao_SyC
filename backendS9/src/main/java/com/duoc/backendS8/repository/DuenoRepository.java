package com.duoc.backendS8.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duoc.backendS8.entity.Dueno;

public interface DuenoRepository extends JpaRepository<Dueno, Long> {

	Optional<Dueno> findByEmailIgnoreCase(String email);
}
