package com.ecomerce.src.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecomerce.src.dto.ProductRequest;
import com.ecomerce.src.entity.Product;
import com.ecomerce.src.entity.User;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.ProductRepository;
import com.ecomerce.src.repository.UserRepository;
import com.ecomerce.src.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	public ProductServiceImpl(ProductRepository productRepository, UserRepository userRepository) {
		this.productRepository = productRepository;
		this.userRepository = userRepository;
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
	public Product crear(ProductRequest request, MultipartFile image) {
		validatePublisherRole(request.getUsuarioId());
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
		validateDiscountDates(request.getDescuentoPorcentaje(), request.getDescuentoInicio(), request.getDescuentoFin());

		User usuario = userRepository.findById(request.getUsuarioId())
				.orElseThrow(() -> new ResourceNotFoundException("No existe el usuario con id " + request.getUsuarioId()));

		product.setUsuario(usuario);
		product.setUsuarioId(request.getUsuarioId());
		product.setCategoriaId(request.getCategoriaId());
		product.setNombre(request.getNombre());
		product.setPrecio(request.getPrecio());
		product.setDescripcion(request.getDescripcion());
		product.setStock(request.getStock());
		product.setDescuentoPorcentaje(request.getDescuentoPorcentaje());
		product.setDescuentoInicio(request.getDescuentoInicio());
		product.setDescuentoFin(request.getDescuentoFin());
	}

	private void validatePublisherRole(Integer usuarioId) {
		User usuario = userRepository.findById(usuarioId)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el usuario con id " + usuarioId));
		String rol = usuario.getRol() == null ? "" : usuario.getRol().trim().toUpperCase(Locale.ROOT);
		if (!"VENDEDOR".equals(rol) && !"ADMINISTRADOR".equals(rol)) {
			throw new IllegalArgumentException("Solo usuarios con rol VENDEDOR o ADMINISTRADOR pueden publicar productos");
		}
		validateAuthenticatedUserCanPublish(usuario);
	}

	private void validateAuthenticatedUserCanPublish(User usuario) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new AccessDeniedException("No autenticado");
		}

		boolean isAdmin = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch("ROLE_ADMINISTRADOR"::equals);
		if (isAdmin) {
			return;
		}

		String authenticatedEmail = authentication.getName() == null ? "" : authentication.getName().trim();
		String requestedEmail = usuario.getEmail() == null ? "" : usuario.getEmail().trim();
		if (!authenticatedEmail.equalsIgnoreCase(requestedEmail)) {
			throw new AccessDeniedException("No tiene permisos para publicar productos en nombre de otro usuario");
		}
	}

	private void validateDiscountDates(BigDecimal descuentoPorcentaje, LocalDateTime descuentoInicio, LocalDateTime descuentoFin) {
		if (descuentoPorcentaje == null && (descuentoInicio != null || descuentoFin != null)) {
			throw new IllegalArgumentException("No se puede definir vigencia de descuento sin porcentaje de descuento");
		}

		if (descuentoInicio != null && descuentoFin != null && descuentoFin.isBefore(descuentoInicio)) {
			throw new IllegalArgumentException("La fecha de fin del descuento no puede ser anterior a la fecha de inicio");
		}
	}
}