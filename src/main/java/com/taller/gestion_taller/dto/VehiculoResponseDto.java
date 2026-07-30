package com.taller.gestion_taller.dto;

public record VehiculoResponseDto(
        Long id,
        String patente,
        String marca,
        String modelo,
        Integer anio
) {
}
