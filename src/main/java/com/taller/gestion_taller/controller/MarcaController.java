package com.taller.gestion_taller.controller;

import com.taller.gestion_taller.dto.MarcaResponseDto;
import com.taller.gestion_taller.service.MarcaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/marcas")
@Tag(name = "Marcas", description = "Gestion de marcas de vehiculos")
public class MarcaController {

    private final MarcaService marcaService;

    public MarcaController(MarcaService marcaService) {
        this.marcaService = marcaService;
    }

    @GetMapping
    @Operation(summary = "Listar marcas de vehiculos")
    public ResponseEntity<List<MarcaResponseDto>> listar() {
        return ResponseEntity.ok(marcaService.listar());
    }
}
