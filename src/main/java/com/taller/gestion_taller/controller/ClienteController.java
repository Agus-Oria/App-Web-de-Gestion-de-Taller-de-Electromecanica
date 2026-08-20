package com.taller.gestion_taller.controller;

import com.taller.gestion_taller.dto.ClienteCreateDto;
import com.taller.gestion_taller.dto.ClienteResponseDto;
import com.taller.gestion_taller.dto.ClienteUpdateDto;
import com.taller.gestion_taller.service.ClienteService;
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
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Gestion de clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @Operation(summary = "Crear cliente")
    public ResponseEntity<ClienteResponseDto> crear(@Valid @RequestBody ClienteCreateDto dto) {
        ClienteResponseDto response = clienteService.crear(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar clientes")
    public ResponseEntity<Page<ClienteResponseDto>> listar(Pageable pageable) {
        return ResponseEntity.ok(clienteService.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por id")
    public ResponseEntity<ClienteResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerPorId(id));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar clientes por nombre o apellido")
    public ResponseEntity<Page<ClienteResponseDto>> buscarPorNombre(
            @RequestParam String texto,
            Pageable pageable) {
        return ResponseEntity.ok(clienteService.buscarPorNombre(texto, pageable));
    }

    @GetMapping("/dni/{dni}")
    @Operation(summary = "Buscar cliente por DNI")
    public ResponseEntity<ClienteResponseDto> buscarPorDni(@PathVariable String dni) {
        return ResponseEntity.ok(clienteService.buscarPorDni(dni));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente")
    public ResponseEntity<ClienteResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteUpdateDto dto) {
        return ResponseEntity.ok(clienteService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
