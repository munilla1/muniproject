package com.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ModificarDTO {

    private String username;
    private String correo;

    @NotBlank(message = "La contraseña actual es obligatoria")
    private String passwordActual;
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String passwordNueva;
    private String passwordNueva2;
}
