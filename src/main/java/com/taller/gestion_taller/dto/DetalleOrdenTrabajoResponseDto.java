package com.taller.gestion_taller.dto;

import java.math.BigDecimal;

public record DetalleOrdenTrabajoResponseDto(
        Long id,
        String descripcion,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal,
        Long ordenTrabajoId
) {
}
