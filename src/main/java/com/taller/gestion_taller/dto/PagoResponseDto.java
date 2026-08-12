package com.taller.gestion_taller.dto;

import com.taller.gestion_taller.entity.MetodoPago;
import java.math.BigDecimal;
import java.time.Instant;

public record PagoResponseDto(
        Long id,
        Instant fechaPago,
        BigDecimal cantidadPagada,
        MetodoPago metodoPago,
        Long ordenTrabajoId
) {
}
