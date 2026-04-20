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

import com.ecomerce.src.dto.DetalleCompraRequest;
import com.ecomerce.src.entity.DetalleCompra;
import com.ecomerce.src.service.DetalleCompraService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/detalle-compras")
@Validated
public class DetalleCompraController {

	private final DetalleCompraService detalleCompraService;

	public DetalleCompraController(DetalleCompraService detalleCompraService) {
		this.detalleCompraService = detalleCompraService;
	}

	@GetMapping
	public ResponseEntity<List<DetalleCompra>> listar() {
		return ResponseEntity.ok(detalleCompraService.listar());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DetalleCompra> obtenerPorId(@PathVariable Integer id) {
		return ResponseEntity.ok(detalleCompraService.obtenerPorId(id));
	}

	@PostMapping
	public ResponseEntity<DetalleCompra> crear(@Valid @RequestBody DetalleCompraRequest request) {
		DetalleCompra creado = detalleCompraService.crear(request);
		return ResponseEntity.created(URI.create("/api/detalle-compras/" + creado.getId())).body(creado);
	}

	@PutMapping("/{id}")
	public ResponseEntity<DetalleCompra> actualizar(@PathVariable Integer id,
			@Valid @RequestBody DetalleCompraRequest request) {
		return ResponseEntity.ok(detalleCompraService.actualizar(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
		detalleCompraService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
