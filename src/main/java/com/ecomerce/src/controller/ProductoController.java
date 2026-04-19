package com.ecomerce.src.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.ecomerce.src.dto.ProductRequest;
import com.ecomerce.src.entity.Product;
import com.ecomerce.src.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
@Validated
public class ProductoController {

	private final ProductService productService;

	public ProductoController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	public ResponseEntity<List<Product>> listar(
			@RequestParam(required = false) Integer usuario,
			@RequestParam(required = false) Integer categoria,
			@RequestParam(required = false) String search,
			@RequestParam(required = false) BigDecimal minPrecio,
			@RequestParam(required = false) BigDecimal maxPrecio) {
		return ResponseEntity.ok(productService.listar(usuario, categoria, search, minPrecio, maxPrecio));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> obtenerPorId(@PathVariable Integer id) {
		return ResponseEntity.ok(productService.obtenerPorId(id));
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Product> crear(@Valid @RequestBody ProductRequest request) {
		Product creado = productService.crear(request);
		return ResponseEntity.created(URI.create("/api/productos/" + creado.getId())).body(creado);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Product> crearConImagen(
			@RequestParam Integer usuarioId,
			@RequestParam(required = false) Integer categoriaId,
			@RequestParam String nombre,
			@RequestParam BigDecimal precio,
			@RequestParam(required = false) String descripcion,
			@RequestParam Integer stock,
			@RequestParam(value = "image", required = false) MultipartFile image) {
		ProductRequest request = buildProductRequest(usuarioId, categoriaId, nombre, precio, descripcion, stock);
		Product creado = productService.crear(request, image);

		return ResponseEntity.created(URI.create("/api/productos/" + creado.getId())).body(creado);
	}

	@PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Product> actualizar(@PathVariable Integer id, @Valid @RequestBody ProductRequest request) {
		return ResponseEntity.ok(productService.actualizar(id, request));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Product> actualizarConImagen(
			@PathVariable Integer id,
			@RequestParam Integer usuarioId,
			@RequestParam(required = false) Integer categoriaId,
			@RequestParam String nombre,
			@RequestParam BigDecimal precio,
			@RequestParam(required = false) String descripcion,
			@RequestParam Integer stock,
			@RequestParam(value = "image", required = false) MultipartFile image) {
		ProductRequest request = buildProductRequest(usuarioId, categoriaId, nombre, precio, descripcion, stock);
		Product actualizado = productService.actualizar(id, request, image);

		return ResponseEntity.ok(actualizado);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarLogico(@PathVariable Integer id) {
		productService.eliminarLogico(id);
		return ResponseEntity.noContent().build();
	}

	private ProductRequest buildProductRequest(
			Integer usuarioId,
			Integer categoriaId,
			String nombre,
			BigDecimal precio,
			String descripcion,
			Integer stock) {
		if (nombre == null || nombre.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
		}

		ProductRequest request = new ProductRequest();
		request.setUsuarioId(usuarioId);
		request.setCategoriaId(categoriaId);
		request.setNombre(nombre);
		request.setPrecio(precio);
		request.setDescripcion(descripcion);
		request.setStock(stock);
		return request;
	}
}