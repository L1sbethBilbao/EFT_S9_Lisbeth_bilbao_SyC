package com.duoc.backendS8.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duoc.backendS8.entity.MensajeSolicitudAdopcion;

public interface MensajeSolicitudAdopcionRepository extends JpaRepository<MensajeSolicitudAdopcion, Long> {

	List<MensajeSolicitudAdopcion> findBySolicitudIdOrderByCreatedAtAsc(Long solicitudId);

	void deleteBySolicitud_Id(Long solicitudId);
}
