package com.taller.gestion_taller.controller;

import com.taller.gestion_taller.dto.CambioEstadoOrdenDto;
import com.taller.gestion_taller.dto.OrdenTrabajoCreateDto;
import com.taller.gestion_taller.dto.OrdenTrabajoResponseDto;
import com.taller.gestion_taller.dto.OrdenTrabajoUpdateDto;
import com.taller.gestion_taller.service.OrdenTrabajoPdfService;
import com.taller.gestion_taller.service.OrdenTrabajoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/ordenes-trabajo")
@Tag(name = "Ordenes de trabajo", description = "Gestion de ordenes de trabajo")
public class OrdenTrabajoController {

    private final OrdenTrabajoService ordenTrabajoService;
    private final OrdenTrabajoPdfService ordenTrabajoPdfService;

    public OrdenTrabajoController(
            OrdenTrabajoService ordenTrabajoService,
            OrdenTrabajoPdfService ordenTrabajoPdfService) {
        this.ordenTrabajoService = ordenTrabajoService;
        this.ordenTrabajoPdfService = ordenTrabajoPdfService;
    }

    @PostMapping
    @Operation(summary = "Crear orden de trabajo")
    public ResponseEntity<OrdenTrabajoResponseDto> crear(@Valid @RequestBody OrdenTrabajoCreateDto dto) {
        OrdenTrabajoResponseDto response = ordenTrabajoService.crear(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar ordenes de trabajo")
    public ResponseEntity<Page<OrdenTrabajoResponseDto>> listar(Pageable pageable) {
        return ResponseEntity.ok(ordenTrabajoService.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener orden de trabajo por id")
    public ResponseEntity<OrdenTrabajoResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordenTrabajoService.obtenerPorId(id));
    }

    @GetMapping("/{id}/detalle")
    @Operation(summary = "Obtener orden de trabajo con todos sus detalles")
    public ResponseEntity<OrdenTrabajoResponseDto> obtenerConDetalles(@PathVariable Long id) {
        return ResponseEntity.ok(ordenTrabajoService.obtenerConDetalles(id));
    }

    @GetMapping("/{id}/pdf")
    @Operation(summary = "Exportar orden de trabajo a PDF")
    public ResponseEntity<byte[]> exportarPdf(@PathVariable Long id) {
        byte[] pdf = ordenTrabajoPdfService.generarPdf(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"orden-" + id + ".pdf\"")
                .body(pdf);
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar ordenes de trabajo de un cliente")
    public ResponseEntity<Page<OrdenTrabajoResponseDto>> listarPorCliente(
            @PathVariable Long clienteId,
            Pageable pageable) {
        return ResponseEntity.ok(ordenTrabajoService.listarPorCliente(clienteId, pageable));
    }

    @GetMapping("/vehiculo/{vehiculoId}")
    @Operation(summary = "Listar ordenes de trabajo de un vehiculo")
    public ResponseEntity<Page<OrdenTrabajoResponseDto>> listarPorVehiculo(
            @PathVariable Long vehiculoId,
            Pageable pageable) {
        return ResponseEntity.ok(ordenTrabajoService.listarPorVehiculo(vehiculoId, pageable));
    }

    @GetMapping("/cliente/dni/{dni}")
    @Operation(summary = "Listar ordenes de trabajo por DNI del cliente")
    public ResponseEntity<Page<OrdenTrabajoResponseDto>> listarPorDniCliente(
            @PathVariable String dni,
            Pageable pageable) {
        return ResponseEntity.ok(ordenTrabajoService.listarPorDniCliente(dni, pageable));
    }

    @GetMapping("/vehiculo/patente/{patente}")
    @Operation(summary = "Listar ordenes de trabajo por patente del vehiculo")
    public ResponseEntity<Page<OrdenTrabajoResponseDto>> listarPorPatenteVehiculo(
            @PathVariable String patente,
            Pageable pageable) {
        return ResponseEntity.ok(ordenTrabajoService.listarPorPatenteVehiculo(patente, pageable));
    }

    @GetMapping("/buscar/cliente")
    @Operation(summary = "Buscar ordenes por DNI, nombre o apellido del cliente")
    public ResponseEntity<Page<OrdenTrabajoResponseDto>> buscarPorCliente(
            @RequestParam String texto,
            Pageable pageable) {
        return ResponseEntity.ok(ordenTrabajoService.buscarPorCliente(texto, pageable));
    }

    @GetMapping("/buscar/patente-vehiculo")
    @Operation(summary = "Buscar ordenes por patente parcial del vehiculo")
    public ResponseEntity<Page<OrdenTrabajoResponseDto>> buscarPorPatenteVehiculo(
            @RequestParam String texto,
            Pageable pageable) {
        return ResponseEntity.ok(ordenTrabajoService.buscarPorPatenteVehiculo(texto, pageable));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de orden de trabajo")
    public ResponseEntity<OrdenTrabajoResponseDto> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambioEstadoOrdenDto dto) {
        return ResponseEntity.ok(ordenTrabajoService.cambiarEstado(id, dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar orden de trabajo")
    public ResponseEntity<OrdenTrabajoResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody OrdenTrabajoUpdateDto dto) {
        return ResponseEntity.ok(ordenTrabajoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar orden de trabajo")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ordenTrabajoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
