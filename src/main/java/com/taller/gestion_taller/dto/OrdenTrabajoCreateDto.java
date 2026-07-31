package com.taller.gestion_taller.dto;

import com.taller.gestion_taller.entity.EstadoOrden;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

public record OrdenTrabajoCreateDto(
        @NotNull Instant fechaIngreso,
        Instant fechaEntrega,
        @Size(max = 1000) String problemaInformado,
        @Size(max = 1000) String diagnostico,
        EstadoOrden estado,
        @NotNull Long clienteId,
        @NotNull Long vehiculoId,
        @Valid List<DetalleOrdenTrabajoUpdateDto> detalles
) {
}
