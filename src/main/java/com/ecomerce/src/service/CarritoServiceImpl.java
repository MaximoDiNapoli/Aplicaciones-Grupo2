package com.ecomerce.src.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecomerce.src.dto.CarritoRequest;
import com.ecomerce.src.entity.Carrito;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.CarritoRepository;

@Service
public class CarritoServiceImpl implements CarritoService {

	private final CarritoRepository carritoRepository;

	public CarritoServiceImpl(CarritoRepository carritoRepository) {
		this.carritoRepository = carritoRepository;
	}

	@Override
	public List<Carrito> listar() {
		return carritoRepository.findAll();
	}

	@Override
	public Carrito obtenerPorId(Integer id) {
		return carritoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el carrito con id " + id));
	}

	@Override
	public Carrito crear(CarritoRequest request) {
		Carrito carrito = new Carrito(request.getNombre(), request.getDescripcion());
		return carritoRepository.save(carrito);
	}

	@Override
	public Carrito actualizar(Integer id, CarritoRequest request) {
		Carrito carrito = carritoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el carrito con id " + id));

		carrito.setNombre(request.getNombre());
		carrito.setDescripcion(request.getDescripcion());

		return carritoRepository.save(carrito);
	}

	@Override
	public void eliminar(Integer id) {
		Carrito carrito = carritoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el carrito con id " + id));
		carritoRepository.delete(carrito);
	}
}
