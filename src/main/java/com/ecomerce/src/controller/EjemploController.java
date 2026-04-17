package com.ecomerce.src.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ejemplo")
public class EjemploController {

	@GetMapping
	public Map<String, Object> ejemplo() {
		return Map.of(
				"mensaje", "Este es un endpoint de ejemplo",
				"proyecto", "Aplicaciones-Grupo2",
				"ruta", "/api/ejemplo");
	}
}