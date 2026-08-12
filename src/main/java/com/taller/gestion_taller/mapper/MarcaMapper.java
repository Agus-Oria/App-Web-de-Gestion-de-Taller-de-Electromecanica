package com.taller.gestion_taller.mapper;

import com.taller.gestion_taller.dto.MarcaResponseDto;
import com.taller.gestion_taller.entity.Marca;
import org.springframework.stereotype.Component;

@Component
public class MarcaMapper {

    public MarcaResponseDto toResponse(Marca marca) {
        return new MarcaResponseDto(
                marca.getId(),
                marca.getNombre());
    }
}
