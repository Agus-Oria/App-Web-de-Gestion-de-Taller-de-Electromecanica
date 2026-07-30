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
        cliente.setNombre(dto.nombre());
        cliente.setApellido(dto.apellido());
        cliente.setTelefono(dto.telefono());
        return cliente;
    }

    public void updateEntity(Cliente cliente, ClienteUpdateDto dto) {
        cliente.setNombre(dto.nombre());
        cliente.setApellido(dto.apellido());
        cliente.setTelefono(dto.telefono());
    }

    public ClienteResponseDto toResponse(Cliente cliente) {
        return new ClienteResponseDto(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getTelefono());
    }
}
