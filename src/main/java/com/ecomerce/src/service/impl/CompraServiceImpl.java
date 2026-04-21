package com.ecomerce.src.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.ecomerce.src.dto.CompraRequest;
import com.ecomerce.src.dto.CompraUpdateRequest;
import com.ecomerce.src.entity.Carrito;
import com.ecomerce.src.entity.Compra;
import com.ecomerce.src.entity.DetalleCarrito;
import com.ecomerce.src.entity.DetalleCompra;
import com.ecomerce.src.entity.DireccionEnvio;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.CarritoRepository;
import com.ecomerce.src.repository.CompraRepository;
import com.ecomerce.src.repository.DetalleCarritoRepository;
import com.ecomerce.src.repository.DetalleCompraRepository;
import com.ecomerce.src.repository.DireccionEnvioRepository;
import com.ecomerce.src.repository.MetodoPagoRepository;
import com.ecomerce.src.security.CurrentUserService;
import com.ecomerce.src.service.CompraService;

@Service
public class CompraServiceImpl implements CompraService {

	private final CompraRepository compraRepository;
	private final DetalleCompraRepository detalleCompraRepository;
	private final CarritoRepository carritoRepository;
	private final DetalleCarritoRepository detalleCarritoRepository;
	private final DireccionEnvioRepository direccionEnvioRepository;
	private final MetodoPagoRepository metodoPagoRepository;
	private final CurrentUserService currentUserService;

	public CompraServiceImpl(
			CompraRepository compraRepository,
			DetalleCompraRepository detalleCompraRepository,
			CarritoRepository carritoRepository,
			DetalleCarritoRepository detalleCarritoRepository,
			DireccionEnvioRepository direccionEnvioRepository,
			MetodoPagoRepository metodoPagoRepository,
			CurrentUserService currentUserService) {
		this.compraRepository = compraRepository;
		this.detalleCompraRepository = detalleCompraRepository;
		this.carritoRepository = carritoRepository;
		this.detalleCarritoRepository = detalleCarritoRepository;
		this.direccionEnvioRepository = direccionEnvioRepository;
		this.metodoPagoRepository = metodoPagoRepository;
		this.currentUserService = currentUserService;
	}

	@Override
	public Compra crearDesdeCarrito(Integer idCarrito, CompraRequest request) {
		Integer currentUserId = currentUserService.getCurrentUserId();
		Carrito carrito = carritoRepository.findById(idCarrito)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el carrito con id " + idCarrito));
		if (!currentUserId.equals(carrito.getUsuarioId())) {
			throw new AccessDeniedException("No tiene permisos para comprar con un carrito de otro usuario");
		}

		DireccionEnvio direccionEnvio = direccionEnvioRepository.findById(request.getIdDireccionEnvio())
				.orElseThrow(() -> new ResourceNotFoundException("No existe la dirección de envío con id " + request.getIdDireccionEnvio()));
		if (!currentUserId.equals(direccionEnvio.getIdUsuario())) {
			throw new AccessDeniedException("No tiene permisos para usar una dirección de envío de otro usuario");
		}

		if (!metodoPagoRepository.existsById(request.getIdMetodoPago())) {
			throw new ResourceNotFoundException("No existe el método de pago con id " + request.getIdMetodoPago());
		}

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
		compra.setIdUsuario(currentUserId);
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
	public List<Compra> listarMisCompras() {
		Integer currentUserId = currentUserService.getCurrentUserId();
		return compraRepository.findByIdUsuario(currentUserId);
	}

	@Override
	public List<Compra> listarPorUsuario(Integer idUsuario) {
		if (!currentUserService.isAdmin() && !idUsuario.equals(currentUserService.getCurrentUserId())) {
			throw new AccessDeniedException("No tiene permisos para consultar compras de otro usuario");
		}
		return compraRepository.findByIdUsuario(idUsuario);
	}

	@Override
	public Compra obtenerPorId(Integer id) {
		Compra compra = compraRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe la compra con id " + id));
		validateOwnership(compra);
		return compra;
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

	@Override
	public void eliminar(Integer id) {
		Compra compra = obtenerPorId(id);
		compraRepository.delete(compra);
	}

	private void validateOwnership(Compra compra) {
		if (currentUserService.isAdmin()) {
			return;
		}

		Integer currentUserId = currentUserService.getCurrentUserId();
		if (!currentUserId.equals(compra.getIdUsuario())) {
			throw new AccessDeniedException("No tiene permisos para acceder a esta compra");
		}
	}
}
