package com.taller.gestion_taller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConfiguracionUpdateDto(
        @NotBlank @Size(max = 100) String valor
) {
}