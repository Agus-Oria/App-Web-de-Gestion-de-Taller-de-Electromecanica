package com.taller.gestion_taller.controller;

import com.taller.gestion_taller.dto.ImagenResponseDto;
import com.taller.gestion_taller.service.ImagenOrdenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "Imagenes de ordenes", description = "Gestion de imagenes de vehiculos en ordenes de trabajo")
public class ImagenOrdenController {

    private final ImagenOrdenService imagenOrdenService;

    public ImagenOrdenController(ImagenOrdenService imagenOrdenService) {
        this.imagenOrdenService = imagenOrdenService;
    }

    @PostMapping("/api/ordenes-trabajo/{ordenTrabajoId}/imagenes")
    @Operation(summary = "Subir imagenes a una orden de trabajo")
    public ResponseEntity<List<ImagenResponseDto>> subir(
            @PathVariable Long ordenTrabajoId,
            @RequestParam("imagenes") MultipartFile[] imagenes) {
        return ResponseEntity.ok(imagenOrdenService.subir(ordenTrabajoId, imagenes));
    }

    @GetMapping("/api/ordenes-trabajo/{ordenTrabajoId}/imagenes")
    @Operation(summary = "Listar imagenes de una orden de trabajo")
    public ResponseEntity<List<ImagenResponseDto>> listar(@PathVariable Long ordenTrabajoId) {
        return ResponseEntity.ok(imagenOrdenService.listar(ordenTrabajoId));
    }

    @DeleteMapping("/api/imagenes/{id}")
    @Operation(summary = "Eliminar una imagen de una orden de trabajo")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        imagenOrdenService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}