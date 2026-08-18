package com.taller.gestion_taller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequestDto(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
        String username,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 4, message = "La contrasena debe tener al menos 4 caracteres")
        String password
) {
}
