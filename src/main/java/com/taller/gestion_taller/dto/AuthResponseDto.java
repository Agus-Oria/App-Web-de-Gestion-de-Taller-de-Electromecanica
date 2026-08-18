package com.taller.gestion_taller.dto;

public record AuthResponseDto(
        String token,
        String username,
        String rol,
        Long expiracionMs
) {
}
