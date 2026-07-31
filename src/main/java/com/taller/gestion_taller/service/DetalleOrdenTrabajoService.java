package com.taller.gestion_taller.service;

import com.taller.gestion_taller.dto.DetalleOrdenTrabajoCreateDto;
import com.taller.gestion_taller.dto.DetalleOrdenTrabajoResponseDto;
import com.taller.gestion_taller.dto.DetalleOrdenTrabajoUpdateDto;
import com.taller.gestion_taller.entity.DetalleOrdenTrabajo;
import com.taller.gestion_taller.entity.OrdenTrabajo;
import com.taller.gestion_taller.exception.ResourceNotFoundException;
import com.taller.gestion_taller.mapper.DetalleOrdenTrabajoMapper;
import com.taller.gestion_taller.repository.DetalleOrdenTrabajoRepository;
import com.taller.gestion_taller.validation.OrdenTrabajoCalculator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DetalleOrdenTrabajoService {

    private final DetalleOrdenTrabajoRepository detalleRepository;
    private final OrdenTrabajoService ordenTrabajoService;
    private final DetalleOrdenTrabajoMapper detalleMapper;
    private final OrdenTrabajoCalculator calculator;

    public DetalleOrdenTrabajoService(
            DetalleOrdenTrabajoRepository detalleRepository,
            OrdenTrabajoService ordenTrabajoService,
            DetalleOrdenTrabajoMapper detalleMapper,
            OrdenTrabajoCalculator calculator) {
        this.detalleRepository = detalleRepository;
        this.ordenTrabajoService = ordenTrabajoService;
        this.detalleMapper = detalleMapper;
        this.calculator = calculator;
    }

    @Transactional
    public DetalleOrdenTrabajoResponseDto crear(DetalleOrdenTrabajoCreateDto dto) {
        OrdenTrabajo ordenTrabajo = ordenTrabajoService.buscarEntidadConDetalles(dto.ordenTrabajoId());
        DetalleOrdenTrabajo detalle = detalleMapper.toEntity(dto);
        detalle.setOrdenTrabajo(ordenTrabajo);
        ordenTrabajo.getDetalles().add(detalle);
        calculator.recalcularTotales(ordenTrabajo);
        ordenTrabajoService.recalcularYGuardar(ordenTrabajo);
        return detalleMapper.toResponse(detalle);
    }

    @Transactional(readOnly = true)
    public Page<DetalleOrdenTrabajoResponseDto> listar(Pageable pageable) {
        return detalleRepository.findAll(pageable).map(detalleMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public DetalleOrdenTrabajoResponseDto obtenerPorId(Long id) {
        return detalleMapper.toResponse(buscarEntidad(id));
    }

    @Transactional(readOnly = true)
    public Page<DetalleOrdenTrabajoResponseDto> listarPorOrden(Long ordenTrabajoId, Pageable pageable) {
        ordenTrabajoService.buscarEntidad(ordenTrabajoId);
        return detalleRepository.findByOrdenTrabajoId(ordenTrabajoId, pageable).map(detalleMapper::toResponse);
    }

    @Transactional
    public DetalleOrdenTrabajoResponseDto actualizar(Long id, DetalleOrdenTrabajoUpdateDto dto) {
        DetalleOrdenTrabajo detalle = buscarEntidad(id);
        detalleMapper.updateEntity(detalle, dto);
        ordenTrabajoService.recalcularYGuardar(detalle.getOrdenTrabajo());
        return detalleMapper.toResponse(detalle);
    }

    @Transactional
    public void eliminar(Long id) {
        DetalleOrdenTrabajo detalle = buscarEntidad(id);
        OrdenTrabajo ordenTrabajo = detalle.getOrdenTrabajo();
        ordenTrabajo.getDetalles().remove(detalle);
        detalleRepository.delete(detalle);
        ordenTrabajoService.recalcularYGuardar(ordenTrabajo);
    }

    @Transactional(readOnly = true)
    public DetalleOrdenTrabajo buscarEntidad(Long id) {
        return detalleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de orden no encontrado con id " + id));
    }
}
