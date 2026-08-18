package com.taller.gestion_taller.service;

import com.taller.gestion_taller.dto.PagoCreateDto;
import com.taller.gestion_taller.dto.PagoResponseDto;
import com.taller.gestion_taller.dto.PagoUpdateDto;
import com.taller.gestion_taller.entity.OrdenTrabajo;
import com.taller.gestion_taller.entity.Pago;
import com.taller.gestion_taller.exception.BadRequestException;
import com.taller.gestion_taller.exception.ResourceNotFoundException;
import com.taller.gestion_taller.mapper.PagoMapper;
import com.taller.gestion_taller.repository.PagoRepository;
import com.taller.gestion_taller.security.CurrentUser;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final OrdenTrabajoService ordenTrabajoService;
    private final PagoMapper pagoMapper;
    private final CurrentUser currentUser;

    public PagoService(
            PagoRepository pagoRepository,
            OrdenTrabajoService ordenTrabajoService,
            PagoMapper pagoMapper,
            CurrentUser currentUser) {
        this.pagoRepository = pagoRepository;
        this.ordenTrabajoService = ordenTrabajoService;
        this.pagoMapper = pagoMapper;
        this.currentUser = currentUser;
    }

    @Transactional
    public PagoResponseDto crear(PagoCreateDto dto) {
        OrdenTrabajo ordenTrabajo = ordenTrabajoService.buscarEntidad(dto.ordenTrabajoId());
        BigDecimal pagadoActualizado = sumarPagos(ordenTrabajo.getId()).add(dto.cantidadPagada()).setScale(2, RoundingMode.HALF_UP);
        validarMontoNoSupereTotal(pagadoActualizado, ordenTrabajo.getTotal());

        Pago pago = pagoMapper.toEntity(dto);
        pago.setOrdenTrabajo(ordenTrabajo);
        Pago guardado = pagoRepository.save(pago);
        actualizarPagadoOrden(ordenTrabajo.getId());
        return pagoMapper.toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public Page<PagoResponseDto> listar(Pageable pageable) {
        return pagoRepository.findByOrdenTrabajoClienteUsuarioId(currentUser.id(), pageable)
                .map(pagoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PagoResponseDto obtenerPorId(Long id) {
        return pagoMapper.toResponse(buscarEntidad(id));
    }

    @Transactional(readOnly = true)
    public Page<PagoResponseDto> listarPorOrden(Long ordenTrabajoId, Pageable pageable) {
        ordenTrabajoService.buscarEntidad(ordenTrabajoId);
        return pagoRepository.findByOrdenTrabajoId(ordenTrabajoId, pageable).map(pagoMapper::toResponse);
    }

    @Transactional
    public PagoResponseDto actualizar(Long id, PagoUpdateDto dto) {
        Pago pago = buscarEntidad(id);
        Long ordenTrabajoId = pago.getOrdenTrabajo().getId();
        BigDecimal pagadoSinPagoActual = sumarPagos(ordenTrabajoId).subtract(pago.getCantidadPagada());
        BigDecimal pagadoActualizado = pagadoSinPagoActual.add(dto.cantidadPagada()).setScale(2, RoundingMode.HALF_UP);
        validarMontoNoSupereTotal(pagadoActualizado, pago.getOrdenTrabajo().getTotal());

        pagoMapper.updateEntity(pago, dto);
        Pago actualizado = pagoRepository.save(pago);
        actualizarPagadoOrden(ordenTrabajoId);
        return pagoMapper.toResponse(actualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        Pago pago = buscarEntidad(id);
        Long ordenTrabajoId = pago.getOrdenTrabajo().getId();
        pagoRepository.delete(pago);
        pagoRepository.flush();
        actualizarPagadoOrden(ordenTrabajoId);
    }

    @Transactional(readOnly = true)
    public Pago buscarEntidad(Long id) {
        return pagoRepository.findByIdAndOrdenTrabajoClienteUsuarioId(id, currentUser.id())
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id " + id));
    }

    private void actualizarPagadoOrden(Long ordenTrabajoId) {
        ordenTrabajoService.actualizarPagado(ordenTrabajoId, sumarPagos(ordenTrabajoId));
    }

    private BigDecimal sumarPagos(Long ordenTrabajoId) {
        return pagoRepository.sumarPagosPorOrden(ordenTrabajoId).setScale(2, RoundingMode.HALF_UP);
    }

    private void validarMontoNoSupereTotal(BigDecimal pagado, BigDecimal total) {
        if (pagado.compareTo(total) > 0) {
            throw new BadRequestException("El monto pagado no puede superar el total de la orden");
        }
    }
}
