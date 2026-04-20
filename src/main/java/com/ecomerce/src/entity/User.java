package com.ecomerce.src.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Usuario")
public class User extends BaseEntity{

    private String nombre;
    private String email;
    private String telefono;

    @Column(name = "password_hash")
    private String passwordHash;

    private String rol;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public User(){};

    public User(String nombre, String email, String telefono, String passwordHash, String rol, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
