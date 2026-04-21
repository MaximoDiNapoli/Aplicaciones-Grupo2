package com.ecomerce.src.service.impl;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.ecomerce.src.dto.DetalleCarritoRequest;
import com.ecomerce.src.entity.Carrito;
import com.ecomerce.src.entity.DetalleCarrito;
import com.ecomerce.src.entity.Product;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.CarritoRepository;
import com.ecomerce.src.repository.DetalleCarritoRepository;
import com.ecomerce.src.repository.ProductRepository;
import com.ecomerce.src.security.CurrentUserService;
import com.ecomerce.src.service.DetalleCarritoService;

@Service
public class DetalleCarritoServiceImpl implements DetalleCarritoService {

	private final DetalleCarritoRepository detalleCarritoRepository;
	private final CarritoRepository carritoRepository;
	private final ProductRepository productRepository;
	private final CurrentUserService currentUserService;

	public DetalleCarritoServiceImpl(DetalleCarritoRepository detalleCarritoRepository,
			CarritoRepository carritoRepository, ProductRepository productRepository, CurrentUserService currentUserService) {
		this.detalleCarritoRepository = detalleCarritoRepository;
		this.carritoRepository = carritoRepository;
		this.productRepository = productRepository;
		this.currentUserService = currentUserService;
	}

	@Override
	public List<DetalleCarrito> obtenerItemsPorCarrito(Integer idCarrito) {
		validateOwnedCarrito(idCarrito);
		return detalleCarritoRepository.findByCarritoId(idCarrito);
	}

	@Override
	public DetalleCarrito obtenerItem(Integer idItem) {
		return detalleCarritoRepository.findById(idItem)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el item del carrito con id " + idItem));
	}

	@Override
	public DetalleCarrito crear(DetalleCarritoRequest request) {
		Carrito carrito = validateOwnedCarrito(request.getIdCarrito());

		Product producto = productRepository.findById(request.getIdProducto())
				.orElseThrow(() -> new ResourceNotFoundException("No existe el producto con id " + request.getIdProducto()));

		DetalleCarrito detalleCarrito = new DetalleCarrito(carrito, producto, request.getCantidad(),
				request.getPrecioUnitario());
		return detalleCarritoRepository.save(detalleCarrito);
	}

	@Override
	public DetalleCarrito actualizar(Integer idItem, DetalleCarritoRequest request) {
		DetalleCarrito detalleCarrito = detalleCarritoRepository.findById(idItem)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el item del carrito con id " + idItem));
		validateCurrentUserOwnsCarrito(detalleCarrito.getCarrito());

		if (request.getIdCarrito() != null && !detalleCarrito.getCarrito().getId().equals(request.getIdCarrito())) {
			Carrito carrito = validateOwnedCarrito(request.getIdCarrito());
			detalleCarrito.setCarrito(carrito);
		}

		if (!detalleCarrito.getProducto().getId().equals(request.getIdProducto())) {
			Product producto = productRepository.findById(request.getIdProducto())
					.orElseThrow(() -> new ResourceNotFoundException("No existe el producto con id " + request.getIdProducto()));
			detalleCarrito.setProducto(producto);
		}

		detalleCarrito.setCantidad(request.getCantidad());
		detalleCarrito.setPrecioUnitario(request.getPrecioUnitario());

		return detalleCarritoRepository.save(detalleCarrito);
	}

	@Override
	public void eliminar(Integer idItem) {
		DetalleCarrito detalleCarrito = detalleCarritoRepository.findById(idItem)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el item del carrito con id " + idItem));
		validateCurrentUserOwnsCarrito(detalleCarrito.getCarrito());
		detalleCarritoRepository.delete(detalleCarrito);
	}

	private Carrito validateOwnedCarrito(Integer idCarrito) {
		Carrito carrito = carritoRepository.findById(idCarrito)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el carrito con id " + idCarrito));
		validateCurrentUserOwnsCarrito(carrito);
		return carrito;
	}

	private void validateCurrentUserOwnsCarrito(Carrito carrito) {
		Integer currentUserId = currentUserService.getCurrentUserId();
		if (!currentUserId.equals(carrito.getUsuarioId())) {
			throw new AccessDeniedException("No tiene permisos para acceder a este carrito");
		}
	}
}
