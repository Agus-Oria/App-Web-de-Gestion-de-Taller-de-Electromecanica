package com.taller.gestion_taller.controller;

import com.taller.gestion_taller.dto.DetalleOrdenTrabajoCreateDto;
import com.taller.gestion_taller.dto.DetalleOrdenTrabajoResponseDto;
import com.taller.gestion_taller.dto.DetalleOrdenTrabajoUpdateDto;
import com.taller.gestion_taller.service.DetalleOrdenTrabajoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/detalles-orden")
@Tag(name = "Detalles de orden", description = "Gestion de detalles de orden de trabajo")
public class DetalleOrdenTrabajoController {

    private final DetalleOrdenTrabajoService detalleService;

    public DetalleOrdenTrabajoController(DetalleOrdenTrabajoService detalleService) {
        this.detalleService = detalleService;
    }

    @PostMapping
    @Operation(summary = "Crear detalle de orden")
    public ResponseEntity<DetalleOrdenTrabajoResponseDto> crear(
            @Valid @RequestBody DetalleOrdenTrabajoCreateDto dto) {
        DetalleOrdenTrabajoResponseDto response = detalleService.crear(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar detalles de orden")
    public ResponseEntity<Page<DetalleOrdenTrabajoResponseDto>> listar(Pageable pageable) {
        return ResponseEntity.ok(detalleService.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de orden por id")
    public ResponseEntity<DetalleOrdenTrabajoResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(detalleService.obtenerPorId(id));
    }

    @GetMapping("/orden")
    @Operation(summary = "Listar detalles de una orden")
    public ResponseEntity<Page<DetalleOrdenTrabajoResponseDto>> listarPorOrden(
            @RequestParam Long ordenTrabajoId,
            Pageable pageable) {
        return ResponseEntity.ok(detalleService.listarPorOrden(ordenTrabajoId, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar detalle de orden")
    public ResponseEntity<DetalleOrdenTrabajoResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DetalleOrdenTrabajoUpdateDto dto) {
        return ResponseEntity.ok(detalleService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar detalle de orden")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detalleService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
