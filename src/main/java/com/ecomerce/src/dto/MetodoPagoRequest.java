package com.ecomerce.src.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MetodoPagoRequest {

    @NotBlank
    @Size(max = 100)
    private String tipo;

    @Size(max = 2000)
    private String descripcion;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
