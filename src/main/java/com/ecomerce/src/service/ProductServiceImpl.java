package com.ecomerce.src.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecomerce.src.dto.ProductRequest;
import com.ecomerce.src.entity.Product;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;

	public ProductServiceImpl(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	public List<Product> listar(Integer usuario, Integer categoria, String search, BigDecimal minPrecio, BigDecimal maxPrecio) {
		Specification<Product> spec = Specification.where(activos());

		if (usuario != null) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("usuarioId"), usuario));
		}

		if (categoria != null) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("categoriaId"), categoria));
		}

		if (search != null && !search.isBlank()) {
			String searchLike = "%" + search.trim().toLowerCase() + "%";
			spec = spec.and((root, query, cb) -> cb.or(
					cb.like(cb.lower(root.get("nombre")), searchLike),
					cb.like(cb.lower(root.get("descripcion")), searchLike)));
		}

		if (minPrecio != null) {
			spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("precio"), minPrecio));
		}

		if (maxPrecio != null) {
			spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("precio"), maxPrecio));
		}

		return productRepository.findAll(spec);
	}

	@Override
	public Product obtenerPorId(Integer id) {
		return productRepository.findByIdAndActivoTrue(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el producto con id " + id));
	}

	@Override
	public Product crear(ProductRequest request) {
		Product product = new Product();
		applyRequest(product, request);
		product.setActivo(true);
		return productRepository.save(product);
	}

	@Override
	public Product crear(ProductRequest request, MultipartFile image) {
		Product product = new Product();
		applyRequest(product, request);
		product.setActivo(true);
		applyImage(product, image);
		return productRepository.save(product);
	}

	@Override
	public Product actualizar(Integer id, ProductRequest request) {
		Product product = obtenerPorId(id);
		applyRequest(product, request);
		return productRepository.save(product);
	}

	@Override
	public Product actualizar(Integer id, ProductRequest request, MultipartFile image) {
		Product product = obtenerPorId(id);
		applyRequest(product, request);
		applyImage(product, image);
		return productRepository.save(product);
	}

	@Override
	public void eliminarLogico(Integer id) {
		Product product = obtenerPorId(id);
		product.setActivo(false);
		productRepository.save(product);
	}

	private Specification<Product> activos() {
		return (root, query, cb) -> cb.isTrue(root.get("activo"));
	}

	private void applyImage(Product product, MultipartFile image) {
		if (image == null || image.isEmpty()) {
			return;
		}

		try {
			product.setFoto(image.getBytes());
		} catch (IOException exception) {
			throw new IllegalStateException("No se pudo leer la foto del producto", exception);
		}
	}

	private void applyRequest(Product product, ProductRequest request) {
		product.setUsuarioId(request.getUsuarioId());
		product.setCategoriaId(request.getCategoriaId());
		product.setNombre(request.getNombre());
		product.setPrecio(request.getPrecio());
		product.setDescripcion(request.getDescripcion());
		product.setStock(request.getStock());
	}
}