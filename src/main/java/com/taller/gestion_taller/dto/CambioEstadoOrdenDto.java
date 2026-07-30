package com.taller.gestion_taller.dto;

import com.taller.gestion_taller.entity.EstadoOrden;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoOrdenDto(@NotNull EstadoOrden estado) {
}
