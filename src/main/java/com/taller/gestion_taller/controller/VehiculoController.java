package com.taller.gestion_taller.controller;

import com.taller.gestion_taller.dto.VehiculoCreateDto;
import com.taller.gestion_taller.dto.VehiculoResponseDto;
import com.taller.gestion_taller.dto.VehiculoUpdateDto;
import com.taller.gestion_taller.service.VehiculoService;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/vehiculos")
@Tag(name = "Vehiculos", description = "Gestion de vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @PostMapping
    @Operation(summary = "Crear vehiculo")
    public ResponseEntity<VehiculoResponseDto> crear(@Valid @RequestBody VehiculoCreateDto dto) {
        VehiculoResponseDto response = vehiculoService.crear(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar vehiculos")
    public ResponseEntity<Page<VehiculoResponseDto>> listar(Pageable pageable) {
        return ResponseEntity.ok(vehiculoService.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener vehiculo por id")
    public ResponseEntity<VehiculoResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vehiculoService.obtenerPorId(id));
    }

    @GetMapping("/patente/{patente}")
    @Operation(summary = "Buscar vehiculo por patente")
    public ResponseEntity<VehiculoResponseDto> buscarPorPatente(@PathVariable String patente) {
        return ResponseEntity.ok(vehiculoService.buscarPorPatente(patente));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar vehiculo")
    public ResponseEntity<VehiculoResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody VehiculoUpdateDto dto) {
        return ResponseEntity.ok(vehiculoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar vehiculo")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        vehiculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
