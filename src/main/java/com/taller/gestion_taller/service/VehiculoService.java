package com.taller.gestion_taller.service;

import com.taller.gestion_taller.dto.VehiculoCreateDto;
import com.taller.gestion_taller.dto.VehiculoResponseDto;
import com.taller.gestion_taller.dto.VehiculoUpdateDto;
import com.taller.gestion_taller.entity.Marca;
import com.taller.gestion_taller.entity.Vehiculo;
import com.taller.gestion_taller.exception.DuplicateResourceException;
import com.taller.gestion_taller.exception.ResourceNotFoundException;
import com.taller.gestion_taller.mapper.VehiculoMapper;
import com.taller.gestion_taller.repository.VehiculoRepository;
import com.taller.gestion_taller.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final VehiculoMapper vehiculoMapper;
    private final MarcaService marcaService;
    private final CurrentUser currentUser;

    public VehiculoService(
            VehiculoRepository vehiculoRepository,
            VehiculoMapper vehiculoMapper,
            MarcaService marcaService,
            CurrentUser currentUser) {
        this.vehiculoRepository = vehiculoRepository;
        this.vehiculoMapper = vehiculoMapper;
        this.marcaService = marcaService;
        this.currentUser = currentUser;
    }

    @Transactional
    public VehiculoResponseDto crear(VehiculoCreateDto dto) {
        validarPatenteDisponible(vehiculoMapper.normalizarPatente(dto.patente()));
        Marca marca = marcaService.buscarEntidad(dto.marcaId());
        Vehiculo vehiculo = vehiculoMapper.toEntity(dto, marca);
        vehiculo.setUsuario(currentUser.obtener());
        return vehiculoMapper.toResponse(vehiculoRepository.save(vehiculo));
    }

    @Transactional(readOnly = true)
    public Page<VehiculoResponseDto> listar(Pageable pageable) {
        return vehiculoRepository.findAllByUsuarioId(currentUser.id(), pageable)
                .map(vehiculoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public VehiculoResponseDto obtenerPorId(Long id) {
        return vehiculoMapper.toResponse(buscarEntidad(id));
    }

    @Transactional(readOnly = true)
    public VehiculoResponseDto buscarPorPatente(String patente) {
        return vehiculoMapper.toResponse(vehiculoRepository
                .findByPatenteIgnoreCaseAndUsuarioId(patente, currentUser.id())
                .orElseThrow(() -> new ResourceNotFoundException("Vehiculo no encontrado con patente " + patente)));
    }

    @Transactional
    public VehiculoResponseDto actualizar(Long id, VehiculoUpdateDto dto) {
        Vehiculo vehiculo = buscarEntidad(id);
        String patente = vehiculoMapper.normalizarPatente(dto.patente());
        if (vehiculoRepository.existsByPatenteIgnoreCaseAndIdNotAndUsuarioId(patente, id, currentUser.id())) {
            throw new DuplicateResourceException("Ya existe un vehiculo con patente " + patente);
        }
        Marca marca = marcaService.buscarEntidad(dto.marcaId());
        vehiculoMapper.updateEntity(vehiculo, dto, marca);
        return vehiculoMapper.toResponse(vehiculoRepository.save(vehiculo));
    }

    @Transactional
    public void eliminar(Long id) {
        Vehiculo vehiculo = buscarEntidad(id);
        vehiculoRepository.delete(vehiculo);
    }

    @Transactional(readOnly = true)
    public Vehiculo buscarEntidad(Long id) {
        return vehiculoRepository.findByIdAndUsuarioId(id, currentUser.id())
                .orElseThrow(() -> new ResourceNotFoundException("Vehiculo no encontrado con id " + id));
    }

    private void validarPatenteDisponible(String patente) {
        if (vehiculoRepository.existsByPatenteIgnoreCaseAndUsuarioId(patente, currentUser.id())) {
            throw new DuplicateResourceException("Ya existe un vehiculo con patente " + patente);
        }
    }
}