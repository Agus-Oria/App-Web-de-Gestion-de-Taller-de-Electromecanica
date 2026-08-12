package com.taller.gestion_taller.service;

import com.taller.gestion_taller.dto.MarcaResponseDto;
import com.taller.gestion_taller.entity.Marca;
import com.taller.gestion_taller.exception.ResourceNotFoundException;
import com.taller.gestion_taller.mapper.MarcaMapper;
import com.taller.gestion_taller.repository.MarcaRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarcaService {

    private final MarcaRepository marcaRepository;
    private final MarcaMapper marcaMapper;

    public MarcaService(MarcaRepository marcaRepository, MarcaMapper marcaMapper) {
        this.marcaRepository = marcaRepository;
        this.marcaMapper = marcaMapper;
    }

    @Transactional(readOnly = true)
    public List<MarcaResponseDto> listar() {
        return marcaRepository.findAll(Sort.by("nombre")).stream()
                .map(marcaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Marca buscarEntidad(Long id) {
        return marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada con id " + id));
    }
}
