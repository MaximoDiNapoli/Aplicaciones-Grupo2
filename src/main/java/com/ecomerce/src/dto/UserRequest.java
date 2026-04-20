package com.ecomerce.src.dto;

import lombok.Data;

@Data
public class UserRequest {
    private String nombre;
    private String email;
    private String telefono;
    private String password;
    private String rol;
}
