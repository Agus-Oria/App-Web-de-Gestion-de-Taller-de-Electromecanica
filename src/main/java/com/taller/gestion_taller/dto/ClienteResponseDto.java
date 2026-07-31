package com.taller.gestion_taller.dto;

public record ClienteResponseDto(
        Long id,
        String nombre,
        String apellido,
        String dni,
        String telefono
) {
}
