package com.taller.gestion_taller.controller;

import com.taller.gestion_taller.dto.PagoCreateDto;
import com.taller.gestion_taller.dto.PagoResponseDto;
import com.taller.gestion_taller.dto.PagoUpdateDto;
import com.taller.gestion_taller.service.PagoService;
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
@RequestMapping("/api/pagos")
@Tag(name = "Pagos", description = "Gestion de pagos de ordenes de trabajo")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    @Operation(summary = "Crear pago")
    public ResponseEntity<PagoResponseDto> crear(@Valid @RequestBody PagoCreateDto dto) {
        PagoResponseDto response = pagoService.crear(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar pagos")
    public ResponseEntity<Page<PagoResponseDto>> listar(Pageable pageable) {
        return ResponseEntity.ok(pagoService.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pago por id")
    public ResponseEntity<PagoResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.obtenerPorId(id));
    }

    @GetMapping("/orden/{ordenTrabajoId}")
    @Operation(summary = "Listar pagos de una orden de trabajo")
    public ResponseEntity<Page<PagoResponseDto>> listarPorOrden(
            @PathVariable Long ordenTrabajoId,
            Pageable pageable) {
        return ResponseEntity.ok(pagoService.listarPorOrden(ordenTrabajoId, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar pago")
    public ResponseEntity<PagoResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PagoUpdateDto dto) {
        return ResponseEntity.ok(pagoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar pago")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pagoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
