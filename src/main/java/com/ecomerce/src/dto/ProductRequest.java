package com.ecomerce.src.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProductRequest {

	@NotNull
	private Integer usuarioId;

	private Integer categoriaId;

	@NotBlank
	@Size(max = 255)
	private String nombre;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = true)
	private BigDecimal precio;

	@Size(max = 4000)
	private String descripcion;

	@NotNull
	@Min(0)
	private Integer stock;

	@DecimalMin(value = "0.0", inclusive = true)
	@DecimalMax(value = "100.0", inclusive = true)
	private BigDecimal descuentoPorcentaje;

	private LocalDateTime descuentoInicio;

	private LocalDateTime descuentoFin;

	public Integer getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Integer usuarioId) {
		this.usuarioId = usuarioId;
	}

	public Integer getCategoriaId() {
		return categoriaId;
	}

	public void setCategoriaId(Integer categoriaId) {
		this.categoriaId = categoriaId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public BigDecimal getPrecio() {
		return precio;
	}

	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public BigDecimal getDescuentoPorcentaje() {
		return descuentoPorcentaje;
	}

	public void setDescuentoPorcentaje(BigDecimal descuentoPorcentaje) {
		this.descuentoPorcentaje = descuentoPorcentaje;
	}

	public LocalDateTime getDescuentoInicio() {
		return descuentoInicio;
	}

	public void setDescuentoInicio(LocalDateTime descuentoInicio) {
		this.descuentoInicio = descuentoInicio;
	}

	public LocalDateTime getDescuentoFin() {
		return descuentoFin;
	}

	public void setDescuentoFin(LocalDateTime descuentoFin) {
		this.descuentoFin = descuentoFin;
	}
}