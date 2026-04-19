package com.ecomerce.src.service;

import java.util.List;

import com.ecomerce.src.dto.DetalleCarritoRequest;
import com.ecomerce.src.entity.DetalleCarrito;

public interface DetalleCarritoService {

	List<DetalleCarrito> obtenerItemsPorCarrito(Integer idCarrito);

	DetalleCarrito obtenerItem(Integer idItem);

	DetalleCarrito crear(DetalleCarritoRequest request);

	DetalleCarrito actualizar(Integer idItem, DetalleCarritoRequest request);

	void eliminar(Integer idItem);
}
