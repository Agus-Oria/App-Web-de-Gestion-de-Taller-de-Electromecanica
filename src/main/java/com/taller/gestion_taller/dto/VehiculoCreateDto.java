package com.taller.gestion_taller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VehiculoCreateDto(
        @NotBlank @Size(max = 20) String patente,
        @NotNull Long marcaId,
        @NotBlank @Size(max = 80) String modelo,
        @NotNull @Min(1900) @Max(2100) Integer anio,
        @Size(max = 30) String color,
        @Size(max = 50) String numeroChasis,
        @Size(max = 50) String numeroMotor
) {
}
