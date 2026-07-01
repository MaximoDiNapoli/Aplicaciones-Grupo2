package com.ecomerce.src.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "`Producto`")
public class Product extends BaseEntity {

	@Column(name = "id_usuario")
	private Integer usuarioId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario", insertable = false, updatable = false)
	@JsonIgnore
	private User usuario;

	@Column(name = "id_categoria")
	private Integer categoriaId;

	@Column(nullable = false)
	private String nombre;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal precio;

	@Column(columnDefinition = "TEXT")
	private String descripcion;

	@Column(nullable = false)
	private Integer stock;

	@Column(name = "descuento_porcentaje", precision = 5, scale = 2)
	private BigDecimal descuentoPorcentaje;

	@Column(name = "descuento_inicio")
	private LocalDateTime descuentoInicio;

	@Column(name = "descuento_fin")
	private LocalDateTime descuentoFin;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@JsonIgnore
	private byte[] foto;

	@Column(nullable = false)
	private Boolean activo;

	@Column(name = "created_at", insertable = false, updatable = false)
	private LocalDateTime createdAt;

	public Product() {
	}

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

	public byte[] getFoto() {
		return foto;
	}

	public void setFoto(byte[] foto) {
		this.foto = foto;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public User getUsuario() {
		return usuario;
	}

	public void setUsuario(User usuario) {
		this.usuario = usuario;
	}

	@Transient
	public BigDecimal getPrecioFinal() {
		if (precio == null || descuentoPorcentaje == null || descuentoPorcentaje.compareTo(BigDecimal.ZERO) <= 0) {
			return precio;
		}

		LocalDateTime ahora = LocalDateTime.now();
		boolean inicioValido = descuentoInicio == null || !ahora.isBefore(descuentoInicio);
		boolean finValido = descuentoFin == null || !ahora.isAfter(descuentoFin);
		if (!inicioValido || !finValido) {
			return precio;
		}

		BigDecimal descuento = precio.multiply(descuentoPorcentaje).divide(new BigDecimal("100"));
		BigDecimal finalPrice = precio.subtract(descuento);
		if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
			return BigDecimal.ZERO;
		}
		return finalPrice;
	}
}