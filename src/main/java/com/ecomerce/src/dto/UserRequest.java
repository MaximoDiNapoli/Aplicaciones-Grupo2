package com.ecomerce.src.dto;

import jakarta.validation.constraints.Email;
 import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 255, message = "El nombre no debe exceder 255 caracteres")
    private String nombre;

    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    @Size(max = 255, message = "El email no debe exceder 255 caracteres")
    private String email;

    @Size(max = 50, message = "El teléfono no debe exceder 50 caracteres")
    private String telefono;

    @Size(min = 6, max = 255, message = "La contraseña debe tener entre 6 y 255 caracteres")
    private String password;

    @Size(max = 50, message = "El rol no debe exceder 50 caracteres")
    private String rol;
}
