package com.taller.gestion_taller.dto;

import com.taller.gestion_taller.entity.EstadoOrden;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record OrdenTrabajoUpdateDto(
        @NotNull Instant fechaIngreso,
        Instant fechaEntrega,
        @Min(0) Long kilometraje,
        @Size(max = 1000) String problemaInformado,
        @Size(max = 1000) String diagnostico,
        @NotNull EstadoOrden estado,
        @NotNull Long clienteId,
        @NotNull Long vehiculoId
) {
}
