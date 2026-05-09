package com.duoc.backendS8.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duoc.backendS8.entity.Factura;

public interface FacturaRepository extends JpaRepository<Factura, Long> {

	Optional<Factura> findByRegistroMedico_Id(Long registroMedicoId);

	Optional<Factura> findByNumeroFactura(String numeroFactura);
}
