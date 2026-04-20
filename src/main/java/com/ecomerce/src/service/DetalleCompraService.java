package com.ecomerce.src.service;

import java.util.List;

import com.ecomerce.src.dto.DetalleCompraRequest;
import com.ecomerce.src.entity.DetalleCompra;

public interface DetalleCompraService {

	List<DetalleCompra> listar();

	DetalleCompra obtenerPorId(Integer id);

	DetalleCompra crear(DetalleCompraRequest request);

	DetalleCompra actualizar(Integer id, DetalleCompraRequest request);

	void eliminar(Integer id);
}
