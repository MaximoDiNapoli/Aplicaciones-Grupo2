package com.ecomerce.src.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecomerce.src.dto.DetalleCarritoRequest;
import com.ecomerce.src.entity.Carrito;
import com.ecomerce.src.entity.DetalleCarrito;
import com.ecomerce.src.entity.Product;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.CarritoRepository;
import com.ecomerce.src.repository.DetalleCarritoRepository;
import com.ecomerce.src.repository.ProductRepository;
import com.ecomerce.src.service.DetalleCarritoService;

@Service
public class DetalleCarritoServiceImpl implements DetalleCarritoService {

	private final DetalleCarritoRepository detalleCarritoRepository;
	private final CarritoRepository carritoRepository;
	private final ProductRepository productRepository;

	public DetalleCarritoServiceImpl(DetalleCarritoRepository detalleCarritoRepository,
			CarritoRepository carritoRepository, ProductRepository productRepository) {
		this.detalleCarritoRepository = detalleCarritoRepository;
		this.carritoRepository = carritoRepository;
		this.productRepository = productRepository;
	}

	@Override
	public List<DetalleCarrito> obtenerItemsPorCarrito(Integer idCarrito) {
		// Verificar que el carrito existe
		carritoRepository.findById(idCarrito)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el carrito con id " + idCarrito));
		return detalleCarritoRepository.findByCarritoId(idCarrito);
	}

	@Override
	public DetalleCarrito obtenerItem(Integer idItem) {
		return detalleCarritoRepository.findById(idItem)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el item del carrito con id " + idItem));
	}

	@Override
	public DetalleCarrito crear(DetalleCarritoRequest request) {
		Carrito carrito = carritoRepository.findById(request.getIdCarrito())
				.orElseThrow(() -> new ResourceNotFoundException("No existe el carrito con id " + request.getIdCarrito()));

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

		// Verificar que el carrito existe si se está actualizando
		if (request.getIdCarrito() != null && !detalleCarrito.getCarrito().getId().equals(request.getIdCarrito())) {
			carritoRepository.findById(request.getIdCarrito())
					.orElseThrow(() -> new ResourceNotFoundException("No existe el carrito con id " + request.getIdCarrito()));
			Carrito carrito = carritoRepository.findById(request.getIdCarrito()).get();
			detalleCarrito.setCarrito(carrito);
		}

		// Verificar que el producto existe
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
		detalleCarritoRepository.delete(detalleCarrito);
	}
}
