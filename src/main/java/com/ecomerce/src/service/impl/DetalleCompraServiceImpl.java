package com.ecomerce.src.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.ecomerce.src.dto.DetalleCompraRequest;
import com.ecomerce.src.entity.Compra;
import com.ecomerce.src.entity.DetalleCompra;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.CompraRepository;
import com.ecomerce.src.repository.DetalleCompraRepository;
import com.ecomerce.src.repository.ProductRepository;
import com.ecomerce.src.security.CurrentUserService;
import com.ecomerce.src.service.DetalleCompraService;

@Service
public class DetalleCompraServiceImpl implements DetalleCompraService {

	private final DetalleCompraRepository detalleCompraRepository;
	private final CompraRepository compraRepository;
	private final ProductRepository productRepository;
	private final CurrentUserService currentUserService;

	public DetalleCompraServiceImpl(
			DetalleCompraRepository detalleCompraRepository,
			CompraRepository compraRepository,
			ProductRepository productRepository,
			CurrentUserService currentUserService) {
		this.detalleCompraRepository = detalleCompraRepository;
		this.compraRepository = compraRepository;
		this.productRepository = productRepository;
		this.currentUserService = currentUserService;
	}

	@Override
	public List<DetalleCompra> listar() {
		if (currentUserService.isAdmin()) {
			return detalleCompraRepository.findAll();
		}
		Integer currentUserId = currentUserService.getCurrentUserId();
		List<Compra> compras = compraRepository.findByIdUsuario(currentUserId);
		List<DetalleCompra> resultado = new ArrayList<>();
		for (Compra compra : compras) {
			resultado.addAll(detalleCompraRepository.findByIdCompra(compra.getId()));
		}
		return resultado;
	}

	@Override
	public DetalleCompra obtenerPorId(Integer id) {
		DetalleCompra detalle = detalleCompraRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el detalle de compra con id " + id));
		validateOwnership(detalle);
		return detalle;
	}

	private void validateOwnership(DetalleCompra detalle) {
		if (currentUserService.isAdmin()) {
			return;
		}
		Compra compra = compraRepository.findById(detalle.getIdCompra())
				.orElseThrow(() -> new ResourceNotFoundException("No existe la compra con id " + detalle.getIdCompra()));
		Integer currentUserId = currentUserService.getCurrentUserId();
		if (!currentUserId.equals(compra.getIdUsuario())) {
			throw new AccessDeniedException("No tiene permisos para acceder a este detalle de compra");
		}
	}

	@Override
	public DetalleCompra crear(DetalleCompraRequest request) {
		validateCompraAndProducto(request);
		DetalleCompra detalleCompra = new DetalleCompra();
		applyRequest(detalleCompra, request);
		return detalleCompraRepository.save(detalleCompra);
	}

	@Override
	public DetalleCompra actualizar(Integer id, DetalleCompraRequest request) {
		DetalleCompra detalleCompra = obtenerPorId(id);
		validateCompraAndProducto(request);
		applyRequest(detalleCompra, request);
		return detalleCompraRepository.save(detalleCompra);
	}

	@Override
	public void eliminar(Integer id) {
		DetalleCompra detalleCompra = obtenerPorId(id);
		detalleCompraRepository.delete(detalleCompra);
	}

	private void validateCompraAndProducto(DetalleCompraRequest request) {
		if (!compraRepository.existsById(request.getIdCompra())) {
			throw new ResourceNotFoundException("No existe la compra con id " + request.getIdCompra());
		}
		if (!productRepository.existsById(request.getIdProducto())) {
			throw new ResourceNotFoundException("No existe el producto con id " + request.getIdProducto());
		}
	}

	private void applyRequest(DetalleCompra detalleCompra, DetalleCompraRequest request) {
		detalleCompra.setIdCompra(request.getIdCompra());
		detalleCompra.setIdProducto(request.getIdProducto());
		detalleCompra.setCantidad(request.getCantidad());
		detalleCompra.setPrecioUnitario(request.getPrecioUnitario());
		detalleCompra.setSubtotal(resolveSubtotal(request));
	}

	private BigDecimal resolveSubtotal(DetalleCompraRequest request) {
		if (request.getSubtotal() != null) {
			return request.getSubtotal();
		}
		return request.getPrecioUnitario().multiply(BigDecimal.valueOf(request.getCantidad()));
	}
}
