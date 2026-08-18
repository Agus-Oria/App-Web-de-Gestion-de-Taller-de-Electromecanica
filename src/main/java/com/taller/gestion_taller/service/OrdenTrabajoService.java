package com.taller.gestion_taller.service;

import com.taller.gestion_taller.dto.CambioEstadoOrdenDto;
import com.taller.gestion_taller.dto.OrdenTrabajoCreateDto;
import com.taller.gestion_taller.dto.OrdenTrabajoResponseDto;
import com.taller.gestion_taller.dto.OrdenTrabajoUpdateDto;
import com.taller.gestion_taller.entity.Cliente;
import com.taller.gestion_taller.entity.EstadoOrden;
import com.taller.gestion_taller.entity.OrdenTrabajo;
import com.taller.gestion_taller.entity.Vehiculo;
import com.taller.gestion_taller.exception.BadRequestException;
import com.taller.gestion_taller.exception.ResourceNotFoundException;
import com.taller.gestion_taller.mapper.OrdenTrabajoMapper;
import com.taller.gestion_taller.repository.OrdenTrabajoRepository;
import com.taller.gestion_taller.security.CurrentUser;
import com.taller.gestion_taller.storage.ImagenStorage;
import com.taller.gestion_taller.validation.OrdenTrabajoCalculator;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdenTrabajoService {

    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final ClienteService clienteService;
    private final VehiculoService vehiculoService;
    private final OrdenTrabajoMapper ordenTrabajoMapper;
    private final OrdenTrabajoCalculator calculator;
    private final ImagenStorage imagenStorage;
    private final CurrentUser currentUser;

    public OrdenTrabajoService(
            OrdenTrabajoRepository ordenTrabajoRepository,
            ClienteService clienteService,
            VehiculoService vehiculoService,
            OrdenTrabajoMapper ordenTrabajoMapper,
            OrdenTrabajoCalculator calculator,
            ImagenStorage imagenStorage,
            CurrentUser currentUser) {
        this.ordenTrabajoRepository = ordenTrabajoRepository;
        this.clienteService = clienteService;
        this.vehiculoService = vehiculoService;
        this.ordenTrabajoMapper = ordenTrabajoMapper;
        this.calculator = calculator;
        this.imagenStorage = imagenStorage;
        this.currentUser = currentUser;
    }

    @Transactional
    public OrdenTrabajoResponseDto crear(OrdenTrabajoCreateDto dto) {
        Cliente cliente = clienteService.buscarEntidad(dto.clienteId());
        Vehiculo vehiculo = vehiculoService.buscarEntidad(dto.vehiculoId());
        OrdenTrabajo ordenTrabajo = ordenTrabajoMapper.toEntity(dto, cliente, vehiculo);
        if (ordenTrabajo.getEstado() == EstadoOrden.FINALIZADA) {
            ordenTrabajo.setFechaEntrega(Instant.now());
        }
        calculator.recalcularTotales(ordenTrabajo);
        validarPagosNoSuperenTotal(ordenTrabajo);
        return ordenTrabajoMapper.toResponse(ordenTrabajoRepository.save(ordenTrabajo));
    }

    @Transactional(readOnly = true)
    public Page<OrdenTrabajoResponseDto> listar(Pageable pageable) {
        return ordenTrabajoRepository.findByClienteUsuarioId(currentUser.id(), pageable)
                .map(ordenTrabajoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public OrdenTrabajoResponseDto obtenerPorId(Long id) {
        return ordenTrabajoMapper.toResponse(buscarEntidad(id));
    }

    @Transactional(readOnly = true)
    public OrdenTrabajoResponseDto obtenerConDetalles(Long id) {
        return ordenTrabajoMapper.toResponse(buscarEntidadConDetalles(id));
    }

    @Transactional(readOnly = true)
    public Page<OrdenTrabajoResponseDto> listarPorCliente(Long clienteId, Pageable pageable) {
        clienteService.buscarEntidad(clienteId);
        return ordenTrabajoRepository.findByClienteIdAndClienteUsuarioId(clienteId, currentUser.id(), pageable)
                .map(ordenTrabajoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrdenTrabajoResponseDto> listarPorVehiculo(Long vehiculoId, Pageable pageable) {
        vehiculoService.buscarEntidad(vehiculoId);
        return ordenTrabajoRepository.findByVehiculoIdAndVehiculoUsuarioId(vehiculoId, currentUser.id(), pageable)
                .map(ordenTrabajoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrdenTrabajoResponseDto> listarPorDniCliente(String dni, Pageable pageable) {
        clienteService.buscarPorDni(dni);
        return ordenTrabajoRepository.findByClienteDniAndClienteUsuarioId(dni.trim(), currentUser.id(), pageable)
                .map(ordenTrabajoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrdenTrabajoResponseDto> listarPorPatenteVehiculo(String patente, Pageable pageable) {
        vehiculoService.buscarPorPatente(patente);
        return ordenTrabajoRepository.findByVehiculoPatenteIgnoreCaseAndVehiculoUsuarioId(
                        patente.trim(), currentUser.id(), pageable)
                .map(ordenTrabajoMapper::toResponse);
    }

    @Transactional
    public OrdenTrabajoResponseDto actualizar(Long id, OrdenTrabajoUpdateDto dto) {
        OrdenTrabajo ordenTrabajo = buscarEntidad(id);
        Cliente cliente = clienteService.buscarEntidad(dto.clienteId());
        Vehiculo vehiculo = vehiculoService.buscarEntidad(dto.vehiculoId());
        ordenTrabajoMapper.updateEntity(ordenTrabajo, dto, cliente, vehiculo);
        calculator.recalcularTotales(ordenTrabajo);
        validarPagosNoSuperenTotal(ordenTrabajo);
        return ordenTrabajoMapper.toResponse(ordenTrabajoRepository.save(ordenTrabajo));
    }

    @Transactional
    public OrdenTrabajoResponseDto cambiarEstado(Long id, CambioEstadoOrdenDto dto) {
        OrdenTrabajo ordenTrabajo = buscarEntidad(id);
        ordenTrabajo.setEstado(dto.estado());
        if (dto.estado() == EstadoOrden.FINALIZADA) {
            ordenTrabajo.setFechaEntrega(Instant.now());
        }
        return ordenTrabajoMapper.toResponse(ordenTrabajoRepository.save(ordenTrabajo));
    }

    @Transactional
    public void eliminar(Long id) {
        OrdenTrabajo ordenTrabajo = buscarEntidad(id);
        ordenTrabajoRepository.delete(ordenTrabajo);
        try {
            imagenStorage.eliminarCarpeta(imagenStorage.carpetaOrden(id));
        } catch (IOException exception) {
            throw new BadRequestException("No se pudieron eliminar las imagenes de la orden");
        }
    }

    @Transactional(readOnly = true)
    public OrdenTrabajo buscarEntidad(Long id) {
        return ordenTrabajoRepository.findByIdAndClienteUsuarioId(id, currentUser.id())
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo no encontrada con id " + id));
    }

    @Transactional(readOnly = true)
    public OrdenTrabajo buscarEntidadConDetalles(Long id) {
        return ordenTrabajoRepository.findWithDetallesByIdAndUsuarioId(id, currentUser.id())
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo no encontrada con id " + id));
    }

    @Transactional
    public void recalcularYGuardar(OrdenTrabajo ordenTrabajo) {
        calculator.recalcularTotales(ordenTrabajo);
        validarPagosNoSuperenTotal(ordenTrabajo);
        ordenTrabajoRepository.save(ordenTrabajo);
    }

    @Transactional
    public void actualizarPagado(Long ordenTrabajoId, BigDecimal pagado) {
        OrdenTrabajo ordenTrabajo = buscarEntidad(ordenTrabajoId);
        ordenTrabajo.setPagado(pagado);
        validarPagosNoSuperenTotal(ordenTrabajo);
        ordenTrabajoRepository.save(ordenTrabajo);
    }

    private void validarPagosNoSuperenTotal(OrdenTrabajo ordenTrabajo) {
        BigDecimal pagado = ordenTrabajo.getPagado() == null ? BigDecimal.ZERO : ordenTrabajo.getPagado();
        if (pagado.compareTo(ordenTrabajo.getTotal()) > 0) {
            throw new BadRequestException("El monto pagado no puede superar el total de la orden");
        }
    }
}
