package com.taller.gestion_taller.mapper;

import com.taller.gestion_taller.dto.VehiculoCreateDto;
import com.taller.gestion_taller.dto.VehiculoResponseDto;
import com.taller.gestion_taller.dto.VehiculoUpdateDto;
import com.taller.gestion_taller.entity.Marca;
import com.taller.gestion_taller.entity.Vehiculo;
import org.springframework.stereotype.Component;

@Component
public class VehiculoMapper {

    private final MarcaMapper marcaMapper;

    public VehiculoMapper(MarcaMapper marcaMapper) {
        this.marcaMapper = marcaMapper;
    }

    public Vehiculo toEntity(VehiculoCreateDto dto, Marca marca) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPatente(normalizarPatente(dto.patente()));
        vehiculo.setMarca(marca);
        vehiculo.setModelo(normalizarModelo(dto.modelo()));
        vehiculo.setAnio(dto.anio());
        vehiculo.setColor(normalizarColor(dto.color()));
        vehiculo.setNumeroChasis(normalizarNumero(dto.numeroChasis()));
        vehiculo.setNumeroMotor(normalizarNumero(dto.numeroMotor()));
        return vehiculo;
    }

    public void updateEntity(Vehiculo vehiculo, VehiculoUpdateDto dto, Marca marca) {
        vehiculo.setPatente(normalizarPatente(dto.patente()));
        vehiculo.setMarca(marca);
        vehiculo.setModelo(normalizarModelo(dto.modelo()));
        vehiculo.setAnio(dto.anio());
        vehiculo.setColor(normalizarColor(dto.color()));
        vehiculo.setNumeroChasis(normalizarNumero(dto.numeroChasis()));
        vehiculo.setNumeroMotor(normalizarNumero(dto.numeroMotor()));
    }

    public VehiculoResponseDto toResponse(Vehiculo vehiculo) {
        return new VehiculoResponseDto(
                vehiculo.getId(),
                vehiculo.getPatente(),
                marcaMapper.toResponse(vehiculo.getMarca()),
                vehiculo.getModelo(),
                vehiculo.getAnio(),
                vehiculo.getColor(),
                vehiculo.getNumeroChasis(),
                vehiculo.getNumeroMotor());
    }

    public String normalizarPatente(String patente) {
        return patente.trim().toUpperCase();
    }

    public String normalizarModelo(String modelo) {
        return modelo.trim().toUpperCase();
    }

    public String normalizarColor(String color) {
        return color == null || color.isBlank() ? null : color.trim().toUpperCase();
    }

    public String normalizarNumero(String numero) {
        return numero == null || numero.isBlank() ? null : numero.trim().toUpperCase();
    }
}
