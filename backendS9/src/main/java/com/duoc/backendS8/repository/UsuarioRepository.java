package com.duoc.backendS8.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.duoc.backendS8.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByUsername(String username);

	@Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.veterinario WHERE u.username = :name")
	Optional<Usuario> findByUsernameFetchingVeterinario(@Param("name") String name);
}
