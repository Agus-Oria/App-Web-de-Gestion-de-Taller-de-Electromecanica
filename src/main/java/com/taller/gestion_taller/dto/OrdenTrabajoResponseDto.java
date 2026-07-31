package com.taller.gestion_taller.dto;

import com.taller.gestion_taller.entity.EstadoOrden;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrdenTrabajoResponseDto(
        Long id,
        Instant fechaIngreso,
        Instant fechaEntrega,
        String problemaInformado,
        String diagnostico,
        EstadoOrden estado,
        BigDecimal total,
        BigDecimal pagado,
        BigDecimal deuda,
        ClienteResponseDto cliente,
        VehiculoResponseDto vehiculo,
        List<DetalleOrdenTrabajoResponseDto> detalles
) {
}
