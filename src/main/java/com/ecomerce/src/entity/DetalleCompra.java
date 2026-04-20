package com.ecomerce.src.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "`DetalleCompra`")
public class DetalleCompra extends BaseEntity {

	@Column(name = "id_compra", nullable = false)
	private Integer idCompra;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_compra", insertable = false, updatable = false)
	@JsonIgnore
	private Compra compra;

	@Column(name = "id_producto", nullable = false)
	private Integer idProducto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_producto", insertable = false, updatable = false)
	@JsonIgnore
	private Product producto;

	@Column(nullable = false)
	private Integer cantidad;

	@Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
	private BigDecimal precioUnitario;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal subtotal;

	public DetalleCompra() {
	}

	public Integer getIdCompra() {
		return idCompra;
	}

	public void setIdCompra(Integer idCompra) {
		this.idCompra = idCompra;
	}

	public Compra getCompra() {
		return compra;
	}

	public void setCompra(Compra compra) {
		this.compra = compra;
	}

	public Integer getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(Integer idProducto) {
		this.idProducto = idProducto;
	}

	public Product getProducto() {
		return producto;
	}

	public void setProducto(Product producto) {
		this.producto = producto;
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

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}
}
