package com.taller.gestion_taller.mapper;

import com.taller.gestion_taller.dto.VehiculoCreateDto;
import com.taller.gestion_taller.dto.VehiculoResponseDto;
import com.taller.gestion_taller.dto.VehiculoUpdateDto;
import com.taller.gestion_taller.entity.Vehiculo;
import org.springframework.stereotype.Component;

@Component
public class VehiculoMapper {

    public Vehiculo toEntity(VehiculoCreateDto dto) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPatente(normalizarPatente(dto.patente()));
        vehiculo.setMarca(dto.marca());
        vehiculo.setModelo(dto.modelo());
        vehiculo.setAnio(dto.anio());
        return vehiculo;
    }

    public void updateEntity(Vehiculo vehiculo, VehiculoUpdateDto dto) {
        vehiculo.setPatente(normalizarPatente(dto.patente()));
        vehiculo.setMarca(dto.marca());
        vehiculo.setModelo(dto.modelo());
        vehiculo.setAnio(dto.anio());
    }

    public VehiculoResponseDto toResponse(Vehiculo vehiculo) {
        return new VehiculoResponseDto(
                vehiculo.getId(),
                vehiculo.getPatente(),
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getAnio());
    }

    public String normalizarPatente(String patente) {
        return patente.trim().toUpperCase();
    }
}
