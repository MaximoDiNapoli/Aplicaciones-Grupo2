package com.ecomerce.src.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Compra")
public class Compra extends BaseEntity {

	@Column(name = "id_carrito")
	private Integer idCarrito;

	@Column(name = "id_usuario", nullable = false)
	private Integer idUsuario;

	@Column(name = "id_estado")
	private Integer idEstado;

	@Column(name = "id_metodo_pago")
	private Integer idMetodoPago;

	@Column(name = "id_direccion_envio")
	private Integer idDireccionEnvio;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal total;

	@Column(name = "fecha_compra", insertable = false, updatable = false)
	private LocalDateTime fechaCompra;

	@Column(name = "updated_at", insertable = false, updatable = false)
	private LocalDateTime updatedAt;

	public Compra() {
	}

	public Integer getIdCarrito() {
		return idCarrito;
	}

	public void setIdCarrito(Integer idCarrito) {
		this.idCarrito = idCarrito;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Integer getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Integer idEstado) {
		this.idEstado = idEstado;
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

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public LocalDateTime getFechaCompra() {
		return fechaCompra;
	}

	public void setFechaCompra(LocalDateTime fechaCompra) {
		this.fechaCompra = fechaCompra;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
