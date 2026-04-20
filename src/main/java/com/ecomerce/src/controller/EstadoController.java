package com.ecomerce.src.controller;

import java.util.List;

import java.net.URI;

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

import com.ecomerce.src.dto.EstadoRequest;
import com.ecomerce.src.entity.Estado;
import com.ecomerce.src.service.EstadoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/estados")
@Validated
public class EstadoController {

	private final EstadoService estadoService;

	public EstadoController(EstadoService estadoService) {
		this.estadoService = estadoService;
	}

	@GetMapping
	public ResponseEntity<List<Estado>> listar() {
		return ResponseEntity.ok(estadoService.listar());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Estado> obtenerPorId(@PathVariable Integer id) {
		return ResponseEntity.ok(estadoService.obtenerPorId(id));
	}

	@PostMapping
	public ResponseEntity<Estado> crear(@Valid @RequestBody EstadoRequest request) {
		Estado creado = estadoService.crear(request);
		return ResponseEntity.created(URI.create("/api/estados/" + creado.getId())).body(creado);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Estado> actualizar(@PathVariable Integer id, @Valid @RequestBody EstadoRequest request) {
		return ResponseEntity.ok(estadoService.actualizar(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
		estadoService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
