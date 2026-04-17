package com.ecomerce.src.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "ejemplo_persistencia")
public class EjemploPersistencia extends BaseEntity {

	@Column(nullable = false)
	private String nombre;

	@Column(nullable = false)
	private String descripcion;

	public EjemploPersistencia() {
	}

	public EjemploPersistencia(String nombre, String descripcion) {
		this.nombre = nombre;
		this.descripcion = descripcion;
	}

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