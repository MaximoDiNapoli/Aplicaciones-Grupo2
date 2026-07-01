package com.ecomerce.src.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "resena")
public class Resena extends BaseEntity {

	@Column(name = "id_producto", nullable = false)
	private Integer idProducto;

	@Column(name = "id_usuario", nullable = false)
	private Integer idUsuario;

	@Column(nullable = false)
	private Integer puntuacion;

	@Column(columnDefinition = "TEXT")
	private String comentario;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	public Resena() {
	}

	public Integer getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(Integer idProducto) {
		this.idProducto = idProducto;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Integer getPuntuacion() {
		return puntuacion;
	}

	public void setPuntuacion(Integer puntuacion) {
		this.puntuacion = puntuacion;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
