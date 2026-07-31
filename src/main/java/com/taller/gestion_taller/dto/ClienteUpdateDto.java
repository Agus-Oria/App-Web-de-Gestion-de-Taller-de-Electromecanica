package com.taller.gestion_taller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteUpdateDto(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 100) String apellido,
        @NotBlank @Size(max = 20) String dni,
        @NotBlank @Size(max = 30) String telefono
) {
}
