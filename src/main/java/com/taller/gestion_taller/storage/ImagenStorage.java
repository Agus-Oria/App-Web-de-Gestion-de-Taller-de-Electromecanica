package com.taller.gestion_taller.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImagenStorage {

    public Path carpetaOrden(Long ordenTrabajoId) {
        return Paths.get("src/main/resources/static/uploads/ordenes", String.valueOf(ordenTrabajoId))
                .toAbsolutePath()
                .normalize();
    }

    public void crearCarpeta(Path carpeta) throws IOException {
        Files.createDirectories(carpeta);
    }

    public void guardar(Path carpeta, String nombreArchivo, MultipartFile archivo) throws IOException {
        archivo.transferTo(carpeta.resolve(nombreArchivo).toFile());
    }

    public void eliminarArchivo(Path archivo) throws IOException {
        Files.deleteIfExists(archivo);
    }

    public void eliminarCarpeta(Path carpeta) throws IOException {
        if (Files.exists(carpeta)) {
            org.springframework.util.FileSystemUtils.deleteRecursively(carpeta);
        }
    }
}