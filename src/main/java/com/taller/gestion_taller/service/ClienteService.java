package com.taller.gestion_taller.service;

import com.taller.gestion_taller.dto.ClienteCreateDto;
import com.taller.gestion_taller.dto.ClienteResponseDto;
import com.taller.gestion_taller.dto.ClienteUpdateDto;
import com.taller.gestion_taller.entity.Cliente;
import com.taller.gestion_taller.exception.DuplicateResourceException;
import com.taller.gestion_taller.exception.ResourceNotFoundException;
import com.taller.gestion_taller.mapper.ClienteMapper;
import com.taller.gestion_taller.repository.ClienteRepository;
import com.taller.gestion_taller.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final CurrentUser currentUser;

    public ClienteService(
            ClienteRepository clienteRepository,
            ClienteMapper clienteMapper,
            CurrentUser currentUser) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.currentUser = currentUser;
    }

    @Transactional
    public ClienteResponseDto crear(ClienteCreateDto dto) {
        String dni = clienteMapper.normalizarDni(dto.dni());
        validarDniDisponible(dni);
        Cliente cliente = clienteMapper.toEntity(dto);
        cliente.setUsuario(currentUser.obtener());
        return clienteMapper.toResponse(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public Page<ClienteResponseDto> listar(Pageable pageable) {
        return clienteRepository.findAllByUsuarioId(currentUser.id(), pageable)
                .map(clienteMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ClienteResponseDto obtenerPorId(Long id) {
        return clienteMapper.toResponse(buscarEntidad(id));
    }

    @Transactional(readOnly = true)
    public Page<ClienteResponseDto> buscarPorNombre(String texto, Pageable pageable) {
        return clienteRepository.buscarPorNombreOApellido(texto.trim(), currentUser.id(), pageable)
                .map(clienteMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ClienteResponseDto> buscarPorDni(String texto, Pageable pageable) {
        return clienteRepository.buscarPorDniContainingIgnoreCase(texto.trim(), currentUser.id(), pageable)
                .map(clienteMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ClienteResponseDto buscarPorDni(String dni) {
        String dniNormalizado = clienteMapper.normalizarDni(dni);
        return clienteMapper.toResponse(clienteRepository.findByDniAndUsuarioId(dniNormalizado, currentUser.id())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con dni " + dniNormalizado)));
    }

    @Transactional
    public ClienteResponseDto actualizar(Long id, ClienteUpdateDto dto) {
        Cliente cliente = buscarEntidad(id);
        String dni = clienteMapper.normalizarDni(dto.dni());
        if (clienteRepository.existsByDniAndIdNotAndUsuarioId(dni, id, currentUser.id())) {
            throw new DuplicateResourceException("Ya existe un cliente con dni " + dni);
        }
        clienteMapper.updateEntity(cliente, dto);
        return clienteMapper.toResponse(clienteRepository.save(cliente));
    }

    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = buscarEntidad(id);
        clienteRepository.delete(cliente);
    }

    @Transactional(readOnly = true)
    public Cliente buscarEntidad(Long id) {
        return clienteRepository.findByIdAndUsuarioId(id, currentUser.id())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id " + id));
    }

    private void validarDniDisponible(String dni) {
        if (clienteRepository.existsByDniAndUsuarioId(dni, currentUser.id())) {
            throw new DuplicateResourceException("Ya existe un cliente con dni " + dni);
        }
    }
}