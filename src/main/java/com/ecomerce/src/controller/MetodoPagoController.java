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

import com.ecomerce.src.dto.MetodoPagoRequest;
import com.ecomerce.src.entity.MetodoPago;
import com.ecomerce.src.service.MetodoPagoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/metodos-pago")
@Validated
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;

    public MetodoPagoController(MetodoPagoService metodoPagoService) {
        this.metodoPagoService = metodoPagoService;
    }

    @GetMapping
    public ResponseEntity<List<MetodoPago>> listar() {
        return ResponseEntity.ok(metodoPagoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetodoPago> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(metodoPagoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<MetodoPago> crear(@Valid @RequestBody MetodoPagoRequest request) {
        MetodoPago creado = metodoPagoService.crear(request);
        return ResponseEntity.created(URI.create("/api/metodos-pago/" + creado.getId())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetodoPago> actualizar(@PathVariable Integer id, @Valid @RequestBody MetodoPagoRequest request) {
        return ResponseEntity.ok(metodoPagoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        metodoPagoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}