package com.ecomerce.src.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Integer id;
    private String nombre;
    private String email;
    private String telefono;
    private String rol;
    private LocalDateTime createdAt;
}
