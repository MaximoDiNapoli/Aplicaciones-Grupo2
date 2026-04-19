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

import com.ecomerce.src.dto.CarritoRequest;
import com.ecomerce.src.entity.Carrito;
import com.ecomerce.src.service.CarritoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/carrito")
@Validated
public class CarritoController {

	private final CarritoService carritoService;

	public CarritoController(CarritoService carritoService) {
		this.carritoService = carritoService;
	}

	@GetMapping
	public ResponseEntity<List<Carrito>> listar() {
		return ResponseEntity.ok(carritoService.listar());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Carrito> obtenerPorId(@PathVariable Integer id) {
		return ResponseEntity.ok(carritoService.obtenerPorId(id));
	}

	@PostMapping
	public ResponseEntity<Carrito> crear(@Valid @RequestBody CarritoRequest request) {
		Carrito creada = carritoService.crear(request);
		return ResponseEntity.created(URI.create("/api/carrito/" + creada.getId())).body(creada);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Carrito> actualizar(@PathVariable Integer id, @Valid @RequestBody CarritoRequest request) {
		return ResponseEntity.ok(carritoService.actualizar(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
		carritoService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}