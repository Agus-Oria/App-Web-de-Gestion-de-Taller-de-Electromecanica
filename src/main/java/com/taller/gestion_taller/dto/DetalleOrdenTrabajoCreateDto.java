package com.taller.gestion_taller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record DetalleOrdenTrabajoCreateDto(
        @NotBlank @Size(max = 500) String descripcion,
        @NotNull @Min(1) Integer cantidad,
        @NotNull @DecimalMin(value = "0.00", inclusive = false) BigDecimal precioUnitario,
        @NotNull Long ordenTrabajoId
) {
}
