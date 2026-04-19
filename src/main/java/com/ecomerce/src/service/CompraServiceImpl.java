package com.ecomerce.src.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ecomerce.src.dto.CompraRequest;
import com.ecomerce.src.dto.CompraUpdateRequest;
import com.ecomerce.src.entity.Compra;
import com.ecomerce.src.entity.DetalleCarrito;
import com.ecomerce.src.entity.DetalleCompra;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.CarritoRepository;
import com.ecomerce.src.repository.CompraRepository;
import com.ecomerce.src.repository.DetalleCarritoRepository;
import com.ecomerce.src.repository.DetalleCompraRepository;

@Service
public class CompraServiceImpl implements CompraService {

	private final CompraRepository compraRepository;
	private final DetalleCompraRepository detalleCompraRepository;
	private final CarritoRepository carritoRepository;
	private final DetalleCarritoRepository detalleCarritoRepository;

	public CompraServiceImpl(
			CompraRepository compraRepository,
			DetalleCompraRepository detalleCompraRepository,
			CarritoRepository carritoRepository,
			DetalleCarritoRepository detalleCarritoRepository) {
		this.compraRepository = compraRepository;
		this.detalleCompraRepository = detalleCompraRepository;
		this.carritoRepository = carritoRepository;
		this.detalleCarritoRepository = detalleCarritoRepository;
	}

	@Override
	public Compra crearDesdeCarrito(Integer idCarrito, CompraRequest request) {
		carritoRepository.findById(idCarrito)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el carrito con id " + idCarrito));

		List<DetalleCarrito> items = detalleCarritoRepository.findByCarritoId(idCarrito);
		if (items.isEmpty()) {
			throw new IllegalArgumentException("El carrito está vacío");
		}

		BigDecimal total = BigDecimal.ZERO;
		for (DetalleCarrito item : items) {
			total = total.add(item.getProducto().getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));
		}

		Compra compra = new Compra();
		compra.setIdCarrito(idCarrito);
		compra.setIdUsuario(request.getIdUsuario());
		compra.setIdMetodoPago(request.getIdMetodoPago());
		compra.setIdDireccionEnvio(request.getIdDireccionEnvio());
		compra.setTotal(total);
		Compra savedCompra = compraRepository.save(compra);

		for (DetalleCarrito item : items) {
			DetalleCompra detalle = new DetalleCompra();
			detalle.setIdCompra(savedCompra.getId());
			detalle.setIdProducto(item.getProducto().getId());
			detalle.setCantidad(item.getCantidad());
			detalle.setPrecioUnitario(item.getProducto().getPrecio());
			detalle.setSubtotal(item.getProducto().getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));
			detalleCompraRepository.save(detalle);
		}

		return savedCompra;
	}

	@Override
	public List<Compra> listarPorUsuario(Integer idUsuario) {
		return compraRepository.findByIdUsuario(idUsuario);
	}

	@Override
	public Compra obtenerPorId(Integer id) {
		return compraRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe la compra con id " + id));
	}

	@Override
	public Compra actualizar(Integer id, CompraUpdateRequest request) {
		Compra compra = obtenerPorId(id);
		compra.setIdEstado(request.getIdEstado());
		return compraRepository.save(compra);
	}

	@Override
	public List<DetalleCompra> obtenerDetalle(Integer idCompra) {
		obtenerPorId(idCompra);
		return detalleCompraRepository.findByIdCompra(idCompra);
	}
}
