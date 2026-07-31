package com.taller.gestion_taller.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PagoResponseDto(
        Long id,
        Instant fechaPago,
        BigDecimal cantidadPagada,
        Long ordenTrabajoId
) {
}
