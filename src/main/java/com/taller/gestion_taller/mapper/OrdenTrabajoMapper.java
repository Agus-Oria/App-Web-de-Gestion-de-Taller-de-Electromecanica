package com.taller.gestion_taller.mapper;

import com.taller.gestion_taller.dto.OrdenTrabajoCreateDto;
import com.taller.gestion_taller.dto.OrdenTrabajoResponseDto;
import com.taller.gestion_taller.dto.OrdenTrabajoUpdateDto;
import com.taller.gestion_taller.dto.DetalleOrdenTrabajoResponseDto;
import com.taller.gestion_taller.entity.Cliente;
import com.taller.gestion_taller.entity.DetalleOrdenTrabajo;
import com.taller.gestion_taller.entity.EstadoOrden;
import com.taller.gestion_taller.entity.OrdenTrabajo;
import com.taller.gestion_taller.entity.Vehiculo;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrdenTrabajoMapper {

    private final ClienteMapper clienteMapper;
    private final VehiculoMapper vehiculoMapper;
    private final DetalleOrdenTrabajoMapper detalleMapper;

    public OrdenTrabajoMapper(
            ClienteMapper clienteMapper,
            VehiculoMapper vehiculoMapper,
            DetalleOrdenTrabajoMapper detalleMapper) {
        this.clienteMapper = clienteMapper;
        this.vehiculoMapper = vehiculoMapper;
        this.detalleMapper = detalleMapper;
    }

    public OrdenTrabajo toEntity(OrdenTrabajoCreateDto dto, Cliente cliente, Vehiculo vehiculo) {
        OrdenTrabajo ordenTrabajo = new OrdenTrabajo();
        ordenTrabajo.setFechaIngreso(dto.fechaIngreso());
        ordenTrabajo.setFechaEntrega(dto.fechaEntrega());
        ordenTrabajo.setProblemaInformado(dto.problemaInformado());
        ordenTrabajo.setDiagnostico(dto.diagnostico());
        ordenTrabajo.setEstado(dto.estado() == null ? EstadoOrden.EN_REPARACION : dto.estado());
        ordenTrabajo.setTotal(BigDecimal.ZERO);
        ordenTrabajo.setCliente(cliente);
        ordenTrabajo.setVehiculo(vehiculo);

        if (dto.detalles() != null) {
            dto.detalles().stream()
                    .map(detalleMapper::toEntity)
                    .forEach(detalle -> agregarDetalle(ordenTrabajo, detalle));
        }

        return ordenTrabajo;
    }

    public void updateEntity(OrdenTrabajo ordenTrabajo, OrdenTrabajoUpdateDto dto, Cliente cliente, Vehiculo vehiculo) {
        ordenTrabajo.setFechaIngreso(dto.fechaIngreso());
        ordenTrabajo.setFechaEntrega(dto.fechaEntrega());
        ordenTrabajo.setProblemaInformado(dto.problemaInformado());
        ordenTrabajo.setDiagnostico(dto.diagnostico());
        ordenTrabajo.setEstado(dto.estado());
        ordenTrabajo.setCliente(cliente);
        ordenTrabajo.setVehiculo(vehiculo);
    }

    public OrdenTrabajoResponseDto toResponse(OrdenTrabajo ordenTrabajo) {
        List<DetalleOrdenTrabajoResponseDto> detalles = ordenTrabajo.getDetalles().stream()
                .map(detalleMapper::toResponse)
                .toList();

        return new OrdenTrabajoResponseDto(
                ordenTrabajo.getId(),
                ordenTrabajo.getFechaIngreso(),
                ordenTrabajo.getFechaEntrega(),
                ordenTrabajo.getProblemaInformado(),
                ordenTrabajo.getDiagnostico(),
                ordenTrabajo.getEstado(),
                ordenTrabajo.getTotal(),
                clienteMapper.toResponse(ordenTrabajo.getCliente()),
                vehiculoMapper.toResponse(ordenTrabajo.getVehiculo()),
                detalles);
    }

    private void agregarDetalle(OrdenTrabajo ordenTrabajo, DetalleOrdenTrabajo detalle) {
        detalle.setOrdenTrabajo(ordenTrabajo);
        ordenTrabajo.getDetalles().add(detalle);
    }
}
