package com.ecomerce.src.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "metodopago")
public class MetodoPago extends BaseEntity {

    @Column(nullable = false)
    private String tipo;

    @Column
    private String descripcion;

    // Constructor vacío (obligatorio para JPA)
    public MetodoPago() {}

    // Constructor para usar en el Service
    public MetodoPago(String tipo, String descripcion) {
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}