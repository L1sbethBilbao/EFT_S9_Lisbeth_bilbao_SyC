package com.duoc.backendS8.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.duoc.backendS8.entity.EstadoSolicitudAdopcion;
import com.duoc.backendS8.entity.SolicitudAdopcion;

public interface SolicitudAdopcionRepository extends JpaRepository<SolicitudAdopcion, Long> {

	Optional<SolicitudAdopcion> findByCodigoSeguimiento(String codigoSeguimiento);

	List<SolicitudAdopcion> findAllByOrderByCreatedAtDesc();

	boolean existsByAnimal_IdAndEmailIgnoreCaseAndEstadoIn(Long animalId, String email,
			List<EstadoSolicitudAdopcion> estados);

	boolean existsByAnimal_IdAndEmailIgnoreCaseAndEstadoInAndIdNot(Long animalId, String email,
			List<EstadoSolicitudAdopcion> estados, Long excludeId);

	@Query("SELECT DISTINCT s FROM SolicitudAdopcion s JOIN FETCH s.animal a WHERE s.estado = :aprobada AND a.dueno IS NULL")
	List<SolicitudAdopcion> findAprobadasConAnimalSinDueno(@Param("aprobada") EstadoSolicitudAdopcion aprobada);
}
