package com.ecomerce.src.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.ecomerce.src.dto.CategoryRequest;
import com.ecomerce.src.entity.Category;
import com.ecomerce.src.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categorias")
@Validated
public class CategoriaController {

	private final CategoryService categoryService;

	public CategoriaController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@GetMapping
	public ResponseEntity<List<Category>> listar() {
		return ResponseEntity.ok(categoryService.listar());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Category> obtenerPorId(@PathVariable Integer id) {
		return ResponseEntity.ok(categoryService.obtenerPorId(id));
	}

	@PostMapping
	public ResponseEntity<Category> crear(@Valid @RequestBody CategoryRequest request) {
		Category creada = categoryService.crear(request);
		return ResponseEntity.created(URI.create("/api/categorias/" + creada.getId())).body(creada);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Category> actualizar(@PathVariable Integer id, @Valid @RequestBody CategoryRequest request) {
		return ResponseEntity.ok(categoryService.actualizar(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
		categoryService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}