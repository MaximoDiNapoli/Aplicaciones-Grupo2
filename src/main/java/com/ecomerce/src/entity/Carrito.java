package com.ecomerce.src.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "`Carrito`")
public class Carrito extends BaseEntity {

	@Column(nullable = false)
	private String nombre;

	@Column
	private String descripcion;

	public Carrito() {
	}

	public Carrito(String nombre, String descripcion) {
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
