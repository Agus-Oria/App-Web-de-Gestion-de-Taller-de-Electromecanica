package com.taller.gestion_taller.mapper;

import com.taller.gestion_taller.dto.ClienteCreateDto;
import com.taller.gestion_taller.dto.ClienteResponseDto;
import com.taller.gestion_taller.dto.ClienteUpdateDto;
import com.taller.gestion_taller.entity.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteCreateDto dto) {
        Cliente cliente = new Cliente();
        cliente.setNombre(normalizarTexto(dto.nombre()));
        cliente.setApellido(normalizarTexto(dto.apellido()));
        cliente.setDni(normalizarDni(dto.dni()));
        cliente.setTelefono(dto.telefono());
        return cliente;
    }

    public void updateEntity(Cliente cliente, ClienteUpdateDto dto) {
        cliente.setNombre(normalizarTexto(dto.nombre()));
        cliente.setApellido(normalizarTexto(dto.apellido()));
        cliente.setDni(normalizarDni(dto.dni()));
        cliente.setTelefono(dto.telefono());
    }

    public ClienteResponseDto toResponse(Cliente cliente) {
        return new ClienteResponseDto(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getDni(),
                cliente.getTelefono());
    }

    public String normalizarDni(String dni) {
        return dni.trim();
    }

    public String normalizarTexto(String texto) {
        return texto.trim().toUpperCase();
    }
}
