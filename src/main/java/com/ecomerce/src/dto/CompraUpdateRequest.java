package com.ecomerce.src.dto;

import jakarta.validation.constraints.NotNull;

public class CompraUpdateRequest {

	@NotNull
	private Integer idEstado;

	public Integer getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Integer idEstado) {
		this.idEstado = idEstado;
	}
}
