package com.ecomerce.src.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import com.ecomerce.src.dto.CarritoRequest;
import com.ecomerce.src.entity.Carrito;
import com.ecomerce.src.entity.User;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.CarritoRepository;
import com.ecomerce.src.repository.UserRepository;
import com.ecomerce.src.security.CurrentUserService;
import com.ecomerce.src.service.CarritoService;

@Service
public class CarritoServiceImpl implements CarritoService {

	private final CarritoRepository carritoRepository;
	private final UserRepository userRepository;
	private final CurrentUserService currentUserService;

	public CarritoServiceImpl(CarritoRepository carritoRepository, UserRepository userRepository, CurrentUserService currentUserService) {
		this.carritoRepository = carritoRepository;
		this.userRepository = userRepository;
		this.currentUserService = currentUserService;
	}

	@Override
	public List<Carrito> listar() {
		Integer currentUserId = currentUserService.getCurrentUserId();
		return carritoRepository.findByUsuarioId(currentUserId);
	}

	@Override
	public List<Carrito> listarPorUsuario(Integer usuarioId) {
		validateUserExists(usuarioId);
		return carritoRepository.findByUsuarioId(usuarioId);
	}

	@Override
	public Carrito obtenerPorId(Integer id) {
		Carrito carrito = carritoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el carrito con id " + id));
		validateCurrentUserOwnsCarrito(carrito);
		return carrito;
	}

	@Override
	public Carrito crear(CarritoRequest request) {
		Integer currentUserId = currentUserService.getCurrentUserId();
		validateUserExists(currentUserId);
		Carrito carrito = new Carrito(currentUserId, request.getNombre());
		return carritoRepository.save(carrito);
	}

	@Override
	public Carrito actualizar(Integer id, CarritoRequest request) {
		Carrito carrito = obtenerPorId(id);
		carrito.setNombre(request.getNombre());

		return carritoRepository.save(carrito);
	}

	@Override
	public void eliminar(Integer id) {
		Carrito carrito = obtenerPorId(id);
		carritoRepository.delete(carrito);
	}

	private void validateCurrentUserOwnsCarrito(Carrito carrito) {
		Integer currentUserId = currentUserService.getCurrentUserId();
		if (!currentUserId.equals(carrito.getUsuarioId())) {
			throw new AccessDeniedException("No tiene permisos para acceder a este carrito");
		}
	}

	private User validateUserExists(Integer usuarioId) {
		return userRepository.findById(usuarioId)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el usuario con id " + usuarioId));
	}
}
