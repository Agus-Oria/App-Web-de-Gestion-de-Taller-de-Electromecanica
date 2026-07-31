package com.taller.gestion_taller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

public record PagoCreateDto(
        Instant fechaPago,
        @NotNull @DecimalMin(value = "0.00", inclusive = false) BigDecimal cantidadPagada,
        @NotNull Long ordenTrabajoId
) {
}
