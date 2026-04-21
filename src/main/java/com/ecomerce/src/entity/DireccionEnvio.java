package com.ecomerce.src.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "direccionenvio")
public class DireccionEnvio extends BaseEntity {

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String ciudad;

    @Column(name = "codigo_postal")
    private String codigoPostal;

    @Column(name = "es_principal")
    private Boolean esPrincipal;

    public DireccionEnvio() {}

    public DireccionEnvio(Integer idUsuario, String direccion, String ciudad, String codigoPostal, Boolean esPrincipal) {
        this.idUsuario = idUsuario;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.codigoPostal = codigoPostal;
        this.esPrincipal = esPrincipal;
    }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public Boolean getEsPrincipal() { return esPrincipal; }
    public void setEsPrincipal(Boolean esPrincipal) { this.esPrincipal = esPrincipal; }
}

