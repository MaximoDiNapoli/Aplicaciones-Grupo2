package com.ecomerce.src.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DireccionEnvioRequest {

    @NotBlank(message = "direccion es requerida")
    @Size(max = 255, message = "direccion no puede exceder 255 caracteres")
    private String direccion;

    @NotBlank(message = "ciudad es requerida")
    @Size(max = 100, message = "ciudad no puede exceder 100 caracteres")
    private String ciudad;

    @Size(max = 20, message = "codigoPostal no puede exceder 20 caracteres")
    private String codigoPostal;

    private Boolean esPrincipal;

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public Boolean getEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(Boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }
}

