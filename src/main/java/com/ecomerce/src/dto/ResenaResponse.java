package com.ecomerce.src.dto;

import java.time.LocalDateTime;

import com.ecomerce.src.entity.Resena;

public class ResenaResponse {

	private Integer id;
	private Integer idProducto;
	private Integer idUsuario;
	private String autorNombre;
	private Integer puntuacion;
	private String comentario;
	private LocalDateTime createdAt;

	public ResenaResponse() {
	}

	public static ResenaResponse from(Resena resena, String autorNombre) {
		ResenaResponse dto = new ResenaResponse();
		dto.id = resena.getId();
		dto.idProducto = resena.getIdProducto();
		dto.idUsuario = resena.getIdUsuario();
		dto.autorNombre = autorNombre;
		dto.puntuacion = resena.getPuntuacion();
		dto.comentario = resena.getComentario();
		dto.createdAt = resena.getCreatedAt();
		return dto;
	}

	public Integer getId() {
		return id;
	}

	public Integer getIdProducto() {
		return idProducto;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public String getAutorNombre() {
		return autorNombre;
	}

	public Integer getPuntuacion() {
		return puntuacion;
	}

	public String getComentario() {
		return comentario;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
