package com.taller.gestion_taller.dto;

public record VehiculoResponseDto(
        Long id,
        String patente,
        MarcaResponseDto marca,
        String modelo,
        Integer anio
) {
}
