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

import com.ecomerce.src.dto.DetalleCarritoRequest;
import com.ecomerce.src.entity.DetalleCarrito;
import com.ecomerce.src.service.DetalleCarritoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/carrito")
@Validated
public class DetalleCarritoController {

	private final DetalleCarritoService detalleCarritoService;

	public DetalleCarritoController(DetalleCarritoService detalleCarritoService) {
		this.detalleCarritoService = detalleCarritoService;
	}

	@GetMapping("/{idCarrito}/items")
	public ResponseEntity<List<DetalleCarrito>> obtenerItems(@PathVariable Integer idCarrito) {
		return ResponseEntity.ok(detalleCarritoService.obtenerItemsPorCarrito(idCarrito));
	}

	@PostMapping("/{idCarrito}/items")
	public ResponseEntity<DetalleCarrito> crearItem(
			@PathVariable Integer idCarrito,
			@Valid @RequestBody DetalleCarritoRequest request) {
		request.setIdCarrito(idCarrito);
		DetalleCarrito creado = detalleCarritoService.crear(request);
		return ResponseEntity.created(URI.create("/api/carrito/items/" + creado.getId())).body(creado);
	}

	@PutMapping("/items/{idItem}")
	public ResponseEntity<DetalleCarrito> actualizarItem(@PathVariable Integer idItem,
			@Valid @RequestBody DetalleCarritoRequest request) {
		return ResponseEntity.ok(detalleCarritoService.actualizar(idItem, request));
	}

	@DeleteMapping("/items/{idItem}")
	public ResponseEntity<Void> eliminarItem(@PathVariable Integer idItem) {
		detalleCarritoService.eliminar(idItem);
		return ResponseEntity.noContent().build();
	}
}
