package com.ecomerce.src.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecomerce.src.dto.CompraRequest;
import com.ecomerce.src.dto.CompraUpdateRequest;
import com.ecomerce.src.entity.Carrito;
import com.ecomerce.src.entity.Compra;
import com.ecomerce.src.entity.DetalleCarrito;
import com.ecomerce.src.entity.DetalleCompra;
import com.ecomerce.src.entity.DireccionEnvio;
import com.ecomerce.src.entity.Product;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.CarritoRepository;
import com.ecomerce.src.repository.CompraRepository;
import com.ecomerce.src.repository.DetalleCarritoRepository;
import com.ecomerce.src.repository.DetalleCompraRepository;
import com.ecomerce.src.repository.DireccionEnvioRepository;
import com.ecomerce.src.repository.EstadoRepository;
import com.ecomerce.src.repository.MetodoPagoRepository;
import com.ecomerce.src.repository.ProductRepository;
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
	private final EstadoRepository estadoRepository;
	private final ProductRepository productRepository;
	private final CurrentUserService currentUserService;

	public CompraServiceImpl(
			CompraRepository compraRepository,
			DetalleCompraRepository detalleCompraRepository,
			CarritoRepository carritoRepository,
			DetalleCarritoRepository detalleCarritoRepository,
			DireccionEnvioRepository direccionEnvioRepository,
			MetodoPagoRepository metodoPagoRepository,
			EstadoRepository estadoRepository,
			ProductRepository productRepository,
			CurrentUserService currentUserService) {
		this.compraRepository = compraRepository;
		this.detalleCompraRepository = detalleCompraRepository;
		this.carritoRepository = carritoRepository;
		this.detalleCarritoRepository = detalleCarritoRepository;
		this.direccionEnvioRepository = direccionEnvioRepository;
		this.metodoPagoRepository = metodoPagoRepository;
		this.estadoRepository = estadoRepository;
		this.productRepository = productRepository;
		this.currentUserService = currentUserService;
	}

	@Override
	@Transactional
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

		for (DetalleCarrito item : items) {
			Product producto = item.getProducto();
			if (producto.getActivo() == null || !producto.getActivo()) {
				throw new IllegalArgumentException("El producto '" + producto.getNombre() + "' ya no está disponible");
			}
			if (producto.getStock() == null || producto.getStock() < item.getCantidad()) {
				throw new IllegalArgumentException("Stock insuficiente para '" + producto.getNombre() + "'. Disponible: "
						+ (producto.getStock() == null ? 0 : producto.getStock()));
			}
		}

		BigDecimal total = BigDecimal.ZERO;
		for (DetalleCarrito item : items) {
			BigDecimal precioUnitario = item.getProducto().getPrecioFinal();
			total = total.add(precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad())));
		}

		Compra compra = new Compra();
		compra.setIdCarrito(idCarrito);
		compra.setIdUsuario(currentUserId);
		compra.setIdMetodoPago(request.getIdMetodoPago());
		compra.setIdDireccionEnvio(request.getIdDireccionEnvio());
		compra.setTotal(total);
		Compra savedCompra = compraRepository.save(compra);

		for (DetalleCarrito item : items) {
			Product producto = item.getProducto();
			BigDecimal precioUnitario = producto.getPrecioFinal();

			DetalleCompra detalle = new DetalleCompra();
			detalle.setIdCompra(savedCompra.getId());
			detalle.setIdProducto(producto.getId());
			detalle.setCantidad(item.getCantidad());
			detalle.setPrecioUnitario(precioUnitario);
			detalle.setSubtotal(precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad())));
			detalleCompraRepository.save(detalle);

			producto.setStock(producto.getStock() - item.getCantidad());
			productRepository.save(producto);
		}

		detalleCarritoRepository.deleteByCarritoId(idCarrito);

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
	@Transactional
	public Compra actualizar(Integer id, CompraUpdateRequest request) {
		if (!currentUserService.isAdminOrVendedor()) {
			throw new AccessDeniedException("Solo un administrador o vendedor puede cambiar el estado de una compra");
		}
		Compra compra = compraRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe la compra con id " + id));
		if (!estadoRepository.existsById(request.getIdEstado())) {
			throw new ResourceNotFoundException("No existe el estado con id " + request.getIdEstado());
		}
		compra.setIdEstado(request.getIdEstado());
		return compraRepository.save(compra);
	}

	@Override
	public List<DetalleCompra> obtenerDetalle(Integer idCompra) {
		obtenerPorId(idCompra);
		return detalleCompraRepository.findByIdCompra(idCompra);
	}

	@Override
	@Transactional
	public void eliminar(Integer id) {
		if (!currentUserService.isAdmin()) {
			throw new AccessDeniedException("Solo un administrador puede eliminar compras");
		}
		Compra compra = compraRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe la compra con id " + id));
		detalleCompraRepository.deleteByIdCompra(id);
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
