package com.ecomerce.src.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
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

	@PostMapping
	public ResponseEntity<Product> crear(@Valid @RequestBody ProductRequest request) {
		Product creado = productService.crear(request);
		return ResponseEntity.created(URI.create("/api/productos/" + creado.getId())).body(creado);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Product> actualizar(@PathVariable Integer id, @Valid @RequestBody ProductRequest request) {
		return ResponseEntity.ok(productService.actualizar(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarLogico(@PathVariable Integer id) {
		productService.eliminarLogico(id);
		return ResponseEntity.noContent().build();
	}
}