package com.ecomerce.src.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DetalleCarritoRequest {

	@NotNull(message = "El ID del carrito no puede ser nulo")
	private Integer idCarrito;

	@NotNull(message = "El ID del producto no puede ser nulo")
	private Integer idProducto;

	@NotNull(message = "La cantidad no puede ser nula")
	@Min(value = 1, message = "La cantidad debe ser mayor a 0")
	private Integer cantidad;

	@NotNull(message = "El precio unitario no puede ser nulo")
	@Min(value = 0, message = "El precio unitario no puede ser negativo")
	private BigDecimal precioUnitario;

	public Integer getIdCarrito() {
		return idCarrito;
	}

	public void setIdCarrito(Integer idCarrito) {
		this.idCarrito = idCarrito;
	}

	public Integer getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(Integer idProducto) {
		this.idProducto = idProducto;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public BigDecimal getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(BigDecimal precioUnitario) {
		this.precioUnitario = precioUnitario;
	}
}
