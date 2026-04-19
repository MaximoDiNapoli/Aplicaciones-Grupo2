package com.ecomerce.src.dto;

import jakarta.validation.constraints.NotNull;

public class CompraRequest {

	@NotNull
	private Integer idUsuario;

	@NotNull
	private Integer idMetodoPago;

	@NotNull
	private Integer idDireccionEnvio;

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Integer getIdMetodoPago() {
		return idMetodoPago;
	}

	public void setIdMetodoPago(Integer idMetodoPago) {
		this.idMetodoPago = idMetodoPago;
	}

	public Integer getIdDireccionEnvio() {
		return idDireccionEnvio;
	}

	public void setIdDireccionEnvio(Integer idDireccionEnvio) {
		this.idDireccionEnvio = idDireccionEnvio;
	}
}
