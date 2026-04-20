package com.ecomerce.src.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ecomerce.src.dto.DetalleCompraRequest;
import com.ecomerce.src.entity.DetalleCompra;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.CompraRepository;
import com.ecomerce.src.repository.DetalleCompraRepository;
import com.ecomerce.src.repository.ProductRepository;
import com.ecomerce.src.service.DetalleCompraService;

@Service
public class DetalleCompraServiceImpl implements DetalleCompraService {

	private final DetalleCompraRepository detalleCompraRepository;
	private final CompraRepository compraRepository;
	private final ProductRepository productRepository;

	public DetalleCompraServiceImpl(
			DetalleCompraRepository detalleCompraRepository,
			CompraRepository compraRepository,
			ProductRepository productRepository) {
		this.detalleCompraRepository = detalleCompraRepository;
		this.compraRepository = compraRepository;
		this.productRepository = productRepository;
	}

	@Override
	public List<DetalleCompra> listar() {
		return detalleCompraRepository.findAll();
	}

	@Override
	public DetalleCompra obtenerPorId(Integer id) {
		return detalleCompraRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el detalle de compra con id " + id));
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
