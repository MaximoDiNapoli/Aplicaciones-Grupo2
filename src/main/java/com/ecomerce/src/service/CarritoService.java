package com.ecomerce.src.service;

import java.util.List;

import com.ecomerce.src.dto.CarritoRequest;
import com.ecomerce.src.entity.Carrito;

public interface CarritoService {

	List<Carrito> listar();

	Carrito obtenerPorId(Integer id);

	Carrito crear(CarritoRequest request);

	Carrito actualizar(Integer id, CarritoRequest request);

	void eliminar(Integer id);
}