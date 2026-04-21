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

import com.ecomerce.src.dto.CompraRequest;
import com.ecomerce.src.dto.CompraUpdateRequest;
import com.ecomerce.src.entity.Compra;
import com.ecomerce.src.entity.DetalleCompra;
import com.ecomerce.src.service.CompraService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Validated
public class CompraController {

	private final CompraService compraService;

	public CompraController(CompraService compraService) {
		this.compraService = compraService;
	}

	@PostMapping("/compras/{idCarrito}")
	public ResponseEntity<Compra> crearDesdeCarrito(
			@PathVariable Integer idCarrito,
			@Valid @RequestBody CompraRequest request) {
		Compra creada = compraService.crearDesdeCarrito(idCarrito, request);
		return ResponseEntity.created(URI.create("/api/compras/" + creada.getId())).body(creada);
	}

	@GetMapping("/compras")
	public ResponseEntity<List<Compra>> listarMisCompras() {
		return ResponseEntity.ok(compraService.listarMisCompras());
	}

	@GetMapping("/compras/{id}")
	public ResponseEntity<Compra> obtenerPorId(@PathVariable Integer id) {
		return ResponseEntity.ok(compraService.obtenerPorId(id));
	}

	@PutMapping("/compras/{id}")
	public ResponseEntity<Compra> actualizar(
			@PathVariable Integer id,
			@Valid @RequestBody CompraUpdateRequest request) {
		return ResponseEntity.ok(compraService.actualizar(id, request));
	}

	@GetMapping("/compras/{id}/detalle")
	public ResponseEntity<List<DetalleCompra>> obtenerDetalle(@PathVariable Integer id) {
		return ResponseEntity.ok(compraService.obtenerDetalle(id));
	}

	@DeleteMapping("/compras/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
		compraService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
