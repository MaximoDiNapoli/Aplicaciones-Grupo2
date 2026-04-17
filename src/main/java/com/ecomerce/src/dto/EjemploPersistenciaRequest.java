package com.ecomerce.src.dto;

import jakarta.validation.constraints.NotBlank;

public class EjemploPersistenciaRequest {

	@NotBlank
	private String nombre;

	@NotBlank
	private String descripcion;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}