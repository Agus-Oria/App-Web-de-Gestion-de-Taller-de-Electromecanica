package com.taller.gestion_taller.mapper;

import com.taller.gestion_taller.dto.DetalleOrdenTrabajoCreateDto;
import com.taller.gestion_taller.dto.DetalleOrdenTrabajoResponseDto;
import com.taller.gestion_taller.dto.DetalleOrdenTrabajoUpdateDto;
import com.taller.gestion_taller.entity.DetalleOrdenTrabajo;
import com.taller.gestion_taller.validation.OrdenTrabajoCalculator;
import org.springframework.stereotype.Component;

@Component
public class DetalleOrdenTrabajoMapper {

    private final OrdenTrabajoCalculator calculator;

    public DetalleOrdenTrabajoMapper(OrdenTrabajoCalculator calculator) {
        this.calculator = calculator;
    }

    public DetalleOrdenTrabajo toEntity(DetalleOrdenTrabajoCreateDto dto) {
        DetalleOrdenTrabajo detalle = new DetalleOrdenTrabajo();
        detalle.setDescripcion(dto.descripcion());
        detalle.setCantidad(dto.cantidad());
        detalle.setPrecioUnitario(dto.precioUnitario());
        detalle.setSubtotal(calculator.calcularSubtotal(dto.cantidad(), dto.precioUnitario()));
        return detalle;
    }

    public DetalleOrdenTrabajo toEntity(DetalleOrdenTrabajoUpdateDto dto) {
        DetalleOrdenTrabajo detalle = new DetalleOrdenTrabajo();
        updateEntity(detalle, dto);
        return detalle;
    }

    public void updateEntity(DetalleOrdenTrabajo detalle, DetalleOrdenTrabajoUpdateDto dto) {
        detalle.setDescripcion(dto.descripcion());
        detalle.setCantidad(dto.cantidad());
        detalle.setPrecioUnitario(dto.precioUnitario());
        detalle.setSubtotal(calculator.calcularSubtotal(dto.cantidad(), dto.precioUnitario()));
    }

    public DetalleOrdenTrabajoResponseDto toResponse(DetalleOrdenTrabajo detalle) {
        return new DetalleOrdenTrabajoResponseDto(
                detalle.getId(),
                detalle.getDescripcion(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getSubtotal(),
                detalle.getOrdenTrabajo().getId());
    }
}
