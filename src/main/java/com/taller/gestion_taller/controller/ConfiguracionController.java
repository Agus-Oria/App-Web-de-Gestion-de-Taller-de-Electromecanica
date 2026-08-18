package com.taller.gestion_taller.controller;

import com.taller.gestion_taller.dto.ConfiguracionResponseDto;
import com.taller.gestion_taller.dto.ConfiguracionUpdateDto;
import com.taller.gestion_taller.service.ConfiguracionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/configuracion")
@Tag(name = "Configuración", description = "Configuración general del taller")
public class ConfiguracionController {

    private final ConfiguracionService configuracionService;

    public ConfiguracionController(ConfiguracionService configuracionService) {
        this.configuracionService = configuracionService;
    }

    @GetMapping("/taller")
    @Operation(summary = "Obtener nombre del taller")
    public ResponseEntity<ConfiguracionResponseDto> obtenerTaller() {
        return ResponseEntity.ok(configuracionService.obtenerTaller());
    }

    @PutMapping("/taller")
    @Operation(summary = "Actualizar nombre del taller")
    public ResponseEntity<ConfiguracionResponseDto> actualizarTaller(@Valid @RequestBody ConfiguracionUpdateDto dto) {
        return ResponseEntity.ok(configuracionService.actualizarTaller(dto.valor()));
    }
}