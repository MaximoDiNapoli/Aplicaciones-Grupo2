package com.ecomerce.src.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ecomerce.src.dto.CompraRequest;
import com.ecomerce.src.dto.CompraUpdateRequest;
import com.ecomerce.src.entity.Carrito;
import com.ecomerce.src.entity.Compra;
import com.ecomerce.src.entity.DetalleCarrito;
import com.ecomerce.src.entity.DetalleCompra;
import com.ecomerce.src.entity.Product;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.CarritoRepository;
import com.ecomerce.src.repository.CompraRepository;
import com.ecomerce.src.repository.DetalleCarritoRepository;
import com.ecomerce.src.repository.DetalleCompraRepository;
import com.ecomerce.src.repository.ProductRepository;

@Service
public class CompraServiceImpl implements CompraService {

	private final CompraRepository compraRepository;
	private final DetalleCompraRepository detalleCompraRepository;
	private final CarritoRepository carritoRepository;
	private final DetalleCarritoRepository detalleCarritoRepository;
	private final ProductRepository productRepository;

	public CompraServiceImpl(
			CompraRepository compraRepository,
			DetalleCompraRepository detalleCompraRepository,
			CarritoRepository carritoRepository,
			DetalleCarritoRepository detalleCarritoRepository,
			ProductRepository productRepository) {
		this.compraRepository = compraRepository;
		this.detalleCompraRepository = detalleCompraRepository;
		this.carritoRepository = carritoRepository;
		this.detalleCarritoRepository = detalleCarritoRepository;
		this.productRepository = productRepository;
	}

	@Override
	public Compra crearDesdeCarrito(Integer idCarrito, CompraRequest request) {
		Carrito carrito = carritoRepository.findById(idCarrito)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el carrito con id " + idCarrito));

		List<DetalleCarrito> items = detalleCarritoRepository.findByIdCarrito(idCarrito);
		if (items.isEmpty()) {
			throw new IllegalArgumentException("El carrito está vacío");
		}

		BigDecimal total = BigDecimal.ZERO;
		List<Product> productos = new ArrayList<>();
		for (DetalleCarrito item : items) {
			Product producto = productRepository.findByIdAndActivoTrue(item.getIdProducto())
					.orElseThrow(() -> new ResourceNotFoundException(
							"No existe el producto con id " + item.getIdProducto()));
			total = total.add(producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));
			productos.add(producto);
		}

		Compra compra = new Compra();
		compra.setIdCarrito(idCarrito);
		compra.setIdUsuario(carrito.getIdUsuario());
		compra.setIdMetodoPago(request.getIdMetodoPago());
		compra.setIdDireccionEnvio(request.getIdDireccionEnvio());
		compra.setTotal(total);
		Compra savedCompra = compraRepository.save(compra);

		for (int i = 0; i < items.size(); i++) {
			DetalleCarrito item = items.get(i);
			Product producto = productos.get(i);

			DetalleCompra detalle = new DetalleCompra();
			detalle.setIdCompra(savedCompra.getId());
			detalle.setIdProducto(item.getIdProducto());
			detalle.setCantidad(item.getCantidad());
			detalle.setPrecioUnitario(producto.getPrecio());
			detalle.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));
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
