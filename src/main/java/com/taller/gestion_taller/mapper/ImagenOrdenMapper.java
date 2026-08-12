package com.taller.gestion_taller.mapper;

import com.taller.gestion_taller.dto.ImagenResponseDto;
import com.taller.gestion_taller.entity.ImagenOrden;
import org.springframework.stereotype.Component;

@Component
public class ImagenOrdenMapper {

    public ImagenResponseDto toResponse(ImagenOrden imagen) {
        return new ImagenResponseDto(
                imagen.getId(),
                imagen.getRuta());
    }
}