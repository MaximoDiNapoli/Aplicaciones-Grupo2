package com.ecomerce.src.controller;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecomerce.src.dto.EjemploPersistenciaRequest;
import com.ecomerce.src.entity.EjemploPersistencia;
import com.ecomerce.src.service.EjemploPersistenciaService;

@RestController
@RequestMapping("/api/ejemplo")
public class EjemploController {

	private final EjemploPersistenciaService service;

	public EjemploController(EjemploPersistenciaService service) {
		this.service = service;
	}

	@GetMapping
	public Map<String, Object> ejemplo() {
		EjemploPersistenciaRequest request = new EjemploPersistenciaRequest();
		request.setNombre("registro-demo");
		request.setDescripcion("Guardado desde /api/ejemplo a las " + OffsetDateTime.now());

		EjemploPersistencia guardado = service.crear(request);

		return Map.of(
				"mensaje", "Se guardo un registro de ejemplo en la base de datos",
				"proyecto", "Aplicaciones-Grupo2",
				"ruta", "/api/ejemplo",
				"id", guardado.getId(),
				"nombre", guardado.getNombre(),
				"descripcion", guardado.getDescripcion());
	}
}