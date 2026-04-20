package com.ecomerce.src.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

import com.ecomerce.src.entity.DireccionEnvio;
import com.ecomerce.src.dto.DireccionEnvioRequest;
import com.ecomerce.src.service.DireccionEnvioService;

@RestController
@RequestMapping("/api/direcciones")
@Validated
public class DireccionEnvioController {

    private final DireccionEnvioService direccionEnvioService;

    public DireccionEnvioController(DireccionEnvioService direccionEnvioService) {
        this.direccionEnvioService = direccionEnvioService;
    }

    @GetMapping
    public ResponseEntity<List<DireccionEnvio>> listar() {
        return ResponseEntity.ok(direccionEnvioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DireccionEnvio> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(direccionEnvioService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<DireccionEnvio> crear(@Valid @RequestBody DireccionEnvioRequest request) {
        DireccionEnvio creado = direccionEnvioService.crear(request);
        return ResponseEntity.created(URI.create("/api/direcciones/" + creado.getId())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DireccionEnvio> actualizar(@PathVariable Integer id, @Valid @RequestBody DireccionEnvioRequest request) {
        return ResponseEntity.ok(direccionEnvioService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        direccionEnvioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

