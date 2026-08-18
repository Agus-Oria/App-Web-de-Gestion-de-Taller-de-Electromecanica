package com.taller.gestion_taller.service;

import com.taller.gestion_taller.dto.ImagenResponseDto;
import com.taller.gestion_taller.entity.ImagenOrden;
import com.taller.gestion_taller.entity.OrdenTrabajo;
import com.taller.gestion_taller.exception.BadRequestException;
import com.taller.gestion_taller.exception.ResourceNotFoundException;
import com.taller.gestion_taller.mapper.ImagenOrdenMapper;
import com.taller.gestion_taller.repository.ImagenOrdenRepository;
import com.taller.gestion_taller.security.CurrentUser;
import com.taller.gestion_taller.storage.ImagenStorage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImagenOrdenService {

    private static final String BASE_RUTA_PUBLICA = "/uploads/ordenes";
    private static final int MAX_IMAGENES_POR_ORDEN = 6;
    private static final long MAX_TAMANIO_BYTES = 5L * 1024 * 1024;
    private static final Set<String> EXTENSIONES_PERMITIDAS = Set.of("jpg", "jpeg", "png", "webp");

    private final ImagenOrdenRepository imagenOrdenRepository;
    private final OrdenTrabajoService ordenTrabajoService;
    private final ImagenOrdenMapper imagenOrdenMapper;
    private final ImagenStorage imagenStorage;
    private final CurrentUser currentUser;

    public ImagenOrdenService(
            ImagenOrdenRepository imagenOrdenRepository,
            OrdenTrabajoService ordenTrabajoService,
            ImagenOrdenMapper imagenOrdenMapper,
            ImagenStorage imagenStorage,
            CurrentUser currentUser) {
        this.imagenOrdenRepository = imagenOrdenRepository;
        this.ordenTrabajoService = ordenTrabajoService;
        this.imagenOrdenMapper = imagenOrdenMapper;
        this.imagenStorage = imagenStorage;
        this.currentUser = currentUser;
    }

    @Transactional
    public List<ImagenResponseDto> subir(Long ordenTrabajoId, MultipartFile[] archivos) {
        OrdenTrabajo ordenTrabajo = ordenTrabajoService.buscarEntidad(ordenTrabajoId);
        if (archivos == null || archivos.length == 0) {
            throw new BadRequestException("Debe enviar al menos una imagen");
        }
        validarLimiteYArchivos(ordenTrabajoId, archivos);

        Path carpeta = imagenStorage.carpetaOrden(ordenTrabajoId);
        try {
            imagenStorage.crearCarpeta(carpeta);
            List<ImagenOrden> guardadas = Arrays.stream(archivos)
                    .map(archivo -> guardarArchivo(ordenTrabajo, carpeta, archivo))
                    .toList();
            return guardadas.stream().map(imagenOrdenMapper::toResponse).toList();
        } catch (IOException exception) {
            throw new BadRequestException("No se pudieron guardar las imagenes");
        }
    }

    @Transactional(readOnly = true)
    public List<ImagenResponseDto> listar(Long ordenTrabajoId) {
        ordenTrabajoService.buscarEntidad(ordenTrabajoId);
        return imagenOrdenRepository.findByOrdenTrabajoIdOrderByIdAsc(ordenTrabajoId).stream()
                .map(imagenOrdenMapper::toResponse)
                .toList();
    }

    @Transactional
    public void eliminar(Long id) {
        ImagenOrden imagen = buscarEntidad(id);
        eliminarArchivo(imagen.getRuta());
        imagenOrdenRepository.delete(imagen);
    }

    @Transactional(readOnly = true)
    public ImagenOrden buscarEntidad(Long id) {
        return imagenOrdenRepository.findByIdAndOrdenTrabajoClienteUsuarioId(id, currentUser.id())
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada con id " + id));
    }

    @Transactional
    public void eliminarPorOrden(Long ordenTrabajoId) {
        try {
            imagenStorage.eliminarCarpeta(imagenStorage.carpetaOrden(ordenTrabajoId));
        } catch (IOException exception) {
            throw new BadRequestException("No se pudieron eliminar las imagenes de la orden");
        }
    }

    private ImagenOrden guardarArchivo(OrdenTrabajo ordenTrabajo, Path carpeta, MultipartFile archivo) {
        String nombreArchivo = UUID.randomUUID() + "." + extension(archivo);
        ImagenOrden imagen = new ImagenOrden();
        imagen.setOrdenTrabajo(ordenTrabajo);
        imagen.setRuta(BASE_RUTA_PUBLICA + "/" + ordenTrabajo.getId() + "/" + nombreArchivo);
        try {
            imagenStorage.guardar(carpeta, nombreArchivo, archivo);
        } catch (IOException exception) {
            throw new BadRequestException("No se pudo guardar la imagen " + archivo.getOriginalFilename());
        }
        return imagenOrdenRepository.save(imagen);
    }

    private void validarLimiteYArchivos(Long ordenTrabajoId, MultipartFile[] archivos) {
        long existentes = imagenOrdenRepository.countByOrdenTrabajoId(ordenTrabajoId);
        if (existentes + archivos.length > MAX_IMAGENES_POR_ORDEN) {
            throw new BadRequestException(
                    "Una orden puede tener como maximo " + MAX_IMAGENES_POR_ORDEN + " imagenes");
        }
        for (MultipartFile archivo : archivos) {
            if (archivo.isEmpty()) {
                throw new BadRequestException("Uno de los archivos esta vacio");
            }
            if (archivo.getSize() > MAX_TAMANIO_BYTES) {
                throw new BadRequestException(
                        "La imagen " + archivo.getOriginalFilename() + " supera el tamanio maximo de 5 MB");
            }
            if (!EXTENSIONES_PERMITIDAS.contains(extension(archivo))) {
                throw new BadRequestException(
                        "La imagen " + archivo.getOriginalFilename()
                                + " tiene un formato no permitido (jpg, jpeg, png, webp)");
            }
        }
    }

    private String extension(MultipartFile archivo) {
        String nombre = archivo.getOriginalFilename();
        int indice = nombre == null ? -1 : nombre.lastIndexOf('.');
        if (indice < 0 || indice == nombre.length() - 1) {
            return "";
        }
        return nombre.substring(indice + 1).toLowerCase(Locale.ROOT);
    }

    private void eliminarArchivo(String ruta) {
        String[] partes = ruta.split("/");
        Long ordenTrabajoId = Long.valueOf(partes[partes.length - 2]);
        String nombreArchivo = partes[partes.length - 1];
        Path archivo = imagenStorage.carpetaOrden(ordenTrabajoId).resolve(nombreArchivo);
        try {
            imagenStorage.eliminarArchivo(archivo);
        } catch (IOException exception) {
            throw new BadRequestException("No se pudo eliminar el archivo de la imagen");
        }
    }
}