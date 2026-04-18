package com.ecomerce.src.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
	public List<Product> listar(Long categoria, String search, BigDecimal minPrecio, BigDecimal maxPrecio) {
		Specification<Product> spec = Specification.where(activos());

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
	public Product obtenerPorId(Long id) {
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
	public Product actualizar(Long id, ProductRequest request) {
		Product product = obtenerPorId(id);
		applyRequest(product, request);
		return productRepository.save(product);
	}

	@Override
	public void eliminarLogico(Long id) {
		Product product = obtenerPorId(id);
		product.setActivo(false);
		productRepository.save(product);
	}

	private Specification<Product> activos() {
		return (root, query, cb) -> cb.isTrue(root.get("activo"));
	}

	private void applyRequest(Product product, ProductRequest request) {
		product.setCategoriaId(request.getCategoriaId());
		product.setNombre(request.getNombre());
		product.setPrecio(request.getPrecio());
		product.setDescripcion(request.getDescripcion());
		product.setStock(request.getStock());
		product.setImagenUrl(request.getImagenUrl());
	}
}