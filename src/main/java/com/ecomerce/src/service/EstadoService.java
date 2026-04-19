package com.ecomerce.src.service;

import java.util.List;

import com.ecomerce.src.dto.EstadoRequest;
import com.ecomerce.src.entity.Estado;

public interface EstadoService {

	List<Estado> listar();

	Estado obtenerPorId(Integer id);

	Estado crear(EstadoRequest request);

	Estado actualizar(Integer id, EstadoRequest request);

	void eliminar(Integer id);
}
