package com.ecomerce.src.service;

import java.util.List;

import com.ecomerce.src.dto.CompraRequest;
import com.ecomerce.src.dto.CompraUpdateRequest;
import com.ecomerce.src.entity.Compra;
import com.ecomerce.src.entity.DetalleCompra;

public interface CompraService {

	Compra crearDesdeCarrito(Integer idCarrito, CompraRequest request);

	List<Compra> listarPorUsuario(Integer idUsuario);

	Compra obtenerPorId(Integer id);

	Compra actualizar(Integer id, CompraUpdateRequest request);

	List<DetalleCompra> obtenerDetalle(Integer idCompra);

	void eliminar(Integer id);
}
