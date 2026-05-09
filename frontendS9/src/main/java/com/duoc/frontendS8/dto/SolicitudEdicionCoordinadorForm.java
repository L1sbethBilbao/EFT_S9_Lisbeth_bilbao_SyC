package com.duoc.frontendS8.dto;

import com.duoc.frontendS8.validation.SafePlainText;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SolicitudEdicionCoordinadorForm {

	@NotBlank
	@SafePlainText
	@Size(max = 200)
	private String nombreCompleto = "";

	@NotBlank
	@Email
	@Size(max = 255)
	private String email = "";

	@SafePlainText
	@Size(max = 40)
	private String telefono = "";

	@SafePlainText
	@Size(max = 300)
	private String direccion = "";

	@SafePlainText
	@Size(max = 120)
	private String ciudad = "";

	@NotBlank
	@SafePlainText
	private String tipoVivienda = "CASA";

	@NotNull
	@Min(1)
	private Integer personasEnHogar = 1;

	private boolean tieneNinos;

	private boolean tieneOtrasMascotas;

	@SafePlainText
	@Size(max = 4000)
	private String experienciaMascotas = "";

	@NotBlank
	@SafePlainText
	@Size(max = 4000)
	private String motivacionAdopcion = "";

	@NotBlank
	@SafePlainText
	@Size(min = 10, max = 2000)
	private String constanciaSolicitante = "";

	public String getNombreCompleto() {
		return nombreCompleto;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public String getTipoVivienda() {
		return tipoVivienda;
	}

	public void setTipoVivienda(String tipoVivienda) {
		this.tipoVivienda = tipoVivienda;
	}

	public Integer getPersonasEnHogar() {
		return personasEnHogar;
	}

	public void setPersonasEnHogar(Integer personasEnHogar) {
		this.personasEnHogar = personasEnHogar;
	}

	public boolean isTieneNinos() {
		return tieneNinos;
	}

	public void setTieneNinos(boolean tieneNinos) {
		this.tieneNinos = tieneNinos;
	}

	public boolean isTieneOtrasMascotas() {
		return tieneOtrasMascotas;
	}

	public void setTieneOtrasMascotas(boolean tieneOtrasMascotas) {
		this.tieneOtrasMascotas = tieneOtrasMascotas;
	}

	public String getExperienciaMascotas() {
		return experienciaMascotas;
	}

	public void setExperienciaMascotas(String experienciaMascotas) {
		this.experienciaMascotas = experienciaMascotas;
	}

	public String getMotivacionAdopcion() {
		return motivacionAdopcion;
	}

	public void setMotivacionAdopcion(String motivacionAdopcion) {
		this.motivacionAdopcion = motivacionAdopcion;
	}

	public String getConstanciaSolicitante() {
		return constanciaSolicitante;
	}

	public void setConstanciaSolicitante(String constanciaSolicitante) {
		this.constanciaSolicitante = constanciaSolicitante;
	}
}
