package com.ecomerce.src.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecomerce.src.dto.CarritoRequest;
import com.ecomerce.src.entity.Carrito;
import com.ecomerce.src.entity.User;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.CarritoRepository;
import com.ecomerce.src.repository.UserRepository;
import com.ecomerce.src.service.CarritoService;

@Service
public class CarritoServiceImpl implements CarritoService {

	private final CarritoRepository carritoRepository;
	private final UserRepository userRepository;

	public CarritoServiceImpl(CarritoRepository carritoRepository, UserRepository userRepository) {
		this.carritoRepository = carritoRepository;
		this.userRepository = userRepository;
	}

	@Override
	public List<Carrito> listar() {
		return carritoRepository.findAll();
	}

	@Override
	public List<Carrito> listarPorUsuario(Integer usuarioId) {
		validateUserExists(usuarioId);
		return carritoRepository.findByUsuarioId(usuarioId);
	}

	@Override
	public Carrito obtenerPorId(Integer id) {
		return carritoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el carrito con id " + id));
	}

	@Override
	public Carrito crear(CarritoRequest request) {
		if (request.getUsuarioId() == null) {
			throw new IllegalArgumentException("El carrito debe estar asociado a un usuario");
		}

		validateUserExists(request.getUsuarioId());
		Carrito carrito = new Carrito(request.getUsuarioId(), request.getNombre(), request.getDescripcion());
		return carritoRepository.save(carrito);
	}

	@Override
	public Carrito actualizar(Integer id, CarritoRequest request) {
		Carrito carrito = carritoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el carrito con id " + id));

		if (request.getUsuarioId() != null) {
			validateUserExists(request.getUsuarioId());
			carrito.setUsuarioId(request.getUsuarioId());
		}

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

	private User validateUserExists(Integer usuarioId) {
		return userRepository.findById(usuarioId)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el usuario con id " + usuarioId));
	}
}
