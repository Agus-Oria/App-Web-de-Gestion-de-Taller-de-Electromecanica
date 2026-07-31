package com.taller.gestion_taller.mapper;

import com.taller.gestion_taller.dto.PagoCreateDto;
import com.taller.gestion_taller.dto.PagoResponseDto;
import com.taller.gestion_taller.dto.PagoUpdateDto;
import com.taller.gestion_taller.entity.Pago;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class PagoMapper {

    public Pago toEntity(PagoCreateDto dto) {
        Pago pago = new Pago();
        pago.setFechaPago(dto.fechaPago() == null ? Instant.now() : dto.fechaPago());
        pago.setCantidadPagada(dto.cantidadPagada());
        return pago;
    }

    public void updateEntity(Pago pago, PagoUpdateDto dto) {
        pago.setFechaPago(dto.fechaPago() == null ? Instant.now() : dto.fechaPago());
        pago.setCantidadPagada(dto.cantidadPagada());
    }

    public PagoResponseDto toResponse(Pago pago) {
        return new PagoResponseDto(
                pago.getId(),
                pago.getFechaPago(),
                pago.getCantidadPagada(),
                pago.getOrdenTrabajo().getId());
    }
}
