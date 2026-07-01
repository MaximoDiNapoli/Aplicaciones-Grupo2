package com.ecomerce.src.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecomerce.src.dto.ResenaRequest;
import com.ecomerce.src.dto.ResenaResponse;
import com.ecomerce.src.service.ResenaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/resenas")
@Validated
public class ResenaController {

	private final ResenaService resenaService;

	public ResenaController(ResenaService resenaService) {
		this.resenaService = resenaService;
	}

	@GetMapping
	public ResponseEntity<List<ResenaResponse>> listar(@RequestParam Integer producto) {
		return ResponseEntity.ok(resenaService.listarPorProducto(producto));
	}

	@PostMapping
	public ResponseEntity<ResenaResponse> crear(@Valid @RequestBody ResenaRequest request) {
		ResenaResponse creada = resenaService.crear(request);
		return ResponseEntity.created(URI.create("/api/resenas/" + creada.getId())).body(creada);
	}
}
