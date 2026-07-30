package com.taller.gestion_taller.service;

import com.taller.gestion_taller.dto.VehiculoCreateDto;
import com.taller.gestion_taller.dto.VehiculoResponseDto;
import com.taller.gestion_taller.dto.VehiculoUpdateDto;
import com.taller.gestion_taller.entity.Vehiculo;
import com.taller.gestion_taller.exception.DuplicateResourceException;
import com.taller.gestion_taller.exception.ResourceNotFoundException;
import com.taller.gestion_taller.mapper.VehiculoMapper;
import com.taller.gestion_taller.repository.VehiculoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final VehiculoMapper vehiculoMapper;

    public VehiculoService(VehiculoRepository vehiculoRepository, VehiculoMapper vehiculoMapper) {
        this.vehiculoRepository = vehiculoRepository;
        this.vehiculoMapper = vehiculoMapper;
    }

    @Transactional
    public VehiculoResponseDto crear(VehiculoCreateDto dto) {
        validarPatenteDisponible(vehiculoMapper.normalizarPatente(dto.patente()));
        return vehiculoMapper.toResponse(vehiculoRepository.save(vehiculoMapper.toEntity(dto)));
    }

    @Transactional(readOnly = true)
    public Page<VehiculoResponseDto> listar(Pageable pageable) {
        return vehiculoRepository.findAll(pageable).map(vehiculoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public VehiculoResponseDto obtenerPorId(Long id) {
        return vehiculoMapper.toResponse(buscarEntidad(id));
    }

    @Transactional(readOnly = true)
    public VehiculoResponseDto buscarPorPatente(String patente) {
        return vehiculoMapper.toResponse(vehiculoRepository.findByPatenteIgnoreCase(patente)
                .orElseThrow(() -> new ResourceNotFoundException("Vehiculo no encontrado con patente " + patente)));
    }

    @Transactional
    public VehiculoResponseDto actualizar(Long id, VehiculoUpdateDto dto) {
        Vehiculo vehiculo = buscarEntidad(id);
        String patente = vehiculoMapper.normalizarPatente(dto.patente());
        if (vehiculoRepository.existsByPatenteIgnoreCaseAndIdNot(patente, id)) {
            throw new DuplicateResourceException("Ya existe un vehiculo con patente " + patente);
        }
        vehiculoMapper.updateEntity(vehiculo, dto);
        return vehiculoMapper.toResponse(vehiculoRepository.save(vehiculo));
    }

    @Transactional
    public void eliminar(Long id) {
        Vehiculo vehiculo = buscarEntidad(id);
        vehiculoRepository.delete(vehiculo);
    }

    @Transactional(readOnly = true)
    public Vehiculo buscarEntidad(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehiculo no encontrado con id " + id));
    }

    private void validarPatenteDisponible(String patente) {
        if (vehiculoRepository.existsByPatenteIgnoreCase(patente)) {
            throw new DuplicateResourceException("Ya existe un vehiculo con patente " + patente);
        }
    }
}
