package com.ecomerce.src.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Compra")
public class Compra extends BaseEntity {

	@Column(name = "id_carrito")
	private Integer idCarrito;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_carrito", insertable = false, updatable = false)
	@JsonIgnore
	private Carrito carrito;

	@Column(name = "id_usuario", nullable = false)
	private Integer idUsuario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario", insertable = false, updatable = false)
	@JsonIgnore
	private User usuario;

	@Column(name = "id_estado")
	private Integer idEstado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estado", insertable = false, updatable = false)
	@JsonIgnore
	private Estado estado;

	@Column(name = "id_metodo_pago")
	private Integer idMetodoPago;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_metodo_pago", insertable = false, updatable = false)
	@JsonIgnore
	private MetodoPago metodoPago;

	@Column(name = "id_direccion_envio")
	private Integer idDireccionEnvio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_direccion_envio", insertable = false, updatable = false)
	@JsonIgnore
	private DireccionEnvio direccionEnvio;

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

	public Carrito getCarrito() {
		return carrito;
	}

	public void setCarrito(Carrito carrito) {
		this.carrito = carrito;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public User getUsuario() {
		return usuario;
	}

	public void setUsuario(User usuario) {
		this.usuario = usuario;
	}

	public Integer getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Integer idEstado) {
		this.idEstado = idEstado;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Integer getIdMetodoPago() {
		return idMetodoPago;
	}

	public void setIdMetodoPago(Integer idMetodoPago) {
		this.idMetodoPago = idMetodoPago;
	}

	public MetodoPago getMetodoPago() {
		return metodoPago;
	}

	public void setMetodoPago(MetodoPago metodoPago) {
		this.metodoPago = metodoPago;
	}

	public Integer getIdDireccionEnvio() {
		return idDireccionEnvio;
	}

	public void setIdDireccionEnvio(Integer idDireccionEnvio) {
		this.idDireccionEnvio = idDireccionEnvio;
	}

	public DireccionEnvio getDireccionEnvio() {
		return direccionEnvio;
	}

	public void setDireccionEnvio(DireccionEnvio direccionEnvio) {
		this.direccionEnvio = direccionEnvio;
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
