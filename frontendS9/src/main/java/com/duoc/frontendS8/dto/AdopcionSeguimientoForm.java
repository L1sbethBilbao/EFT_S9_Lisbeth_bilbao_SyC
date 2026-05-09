package com.duoc.frontendS8.dto;

import com.duoc.frontendS8.validation.SafePlainText;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdopcionSeguimientoForm {

	@NotBlank
	@SafePlainText
	@Size(max = 40)
	private String codigoSeguimiento = "";

	@NotBlank
	@SafePlainText
	@Size(max = 4000)
	private String cuerpo = "";

	public String getCodigoSeguimiento() {
		return codigoSeguimiento;
	}

	public void setCodigoSeguimiento(String codigoSeguimiento) {
		this.codigoSeguimiento = codigoSeguimiento;
	}

	public String getCuerpo() {
		return cuerpo;
	}

	public void setCuerpo(String cuerpo) {
		this.cuerpo = cuerpo;
	}
}
