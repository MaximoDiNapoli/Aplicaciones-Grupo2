package com.ecomerce.src.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecomerce.src.dto.EjemploPersistenciaRequest;
import com.ecomerce.src.entity.EjemploPersistencia;
import com.ecomerce.src.service.EjemploPersistenciaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ejemplo/persistencia")
@Validated
public class EjemploPersistenciaController {

	private final EjemploPersistenciaService service;

	public EjemploPersistenciaController(EjemploPersistenciaService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<EjemploPersistencia> crear(@Valid @RequestBody EjemploPersistenciaRequest request) {
		EjemploPersistencia creado = service.crear(request);
		return ResponseEntity.created(URI.create("/api/ejemplo/persistencia/" + creado.getId())).body(creado);
	}

	@GetMapping
	public ResponseEntity<List<EjemploPersistencia>> listar() {
		return ResponseEntity.ok(service.listar());
	}

	@GetMapping("/{id}")
	public ResponseEntity<EjemploPersistencia> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(service.buscarPorId(id));
	}
}