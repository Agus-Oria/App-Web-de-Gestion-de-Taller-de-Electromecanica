package com.taller.gestion_taller.service;

import com.taller.gestion_taller.dto.ConfiguracionResponseDto;
import com.taller.gestion_taller.entity.Configuracion;
import com.taller.gestion_taller.repository.ConfiguracionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfiguracionService {

    public static final String CLAVE_NOMBRE_TALLER = "taller.nombre";
    private static final String NOMBRE_TALLER_DEFAULT = "GESTIÓN TALLER";

    private final ConfiguracionRepository configuracionRepository;

    public ConfiguracionService(ConfiguracionRepository configuracionRepository) {
        this.configuracionRepository = configuracionRepository;
    }

    @Transactional(readOnly = true)
    public String obtenerNombreTaller() {
        return configuracionRepository.findByClave(CLAVE_NOMBRE_TALLER)
                .map(Configuracion::getValor)
                .orElse(NOMBRE_TALLER_DEFAULT);
    }

    @Transactional(readOnly = true)
    public ConfiguracionResponseDto obtenerTaller() {
        return new ConfiguracionResponseDto(CLAVE_NOMBRE_TALLER, obtenerNombreTaller());
    }

    @Transactional
    public ConfiguracionResponseDto actualizarTaller(String nombre) {
        Configuracion configuracion = configuracionRepository.findByClave(CLAVE_NOMBRE_TALLER)
                .orElseGet(() -> {
                    Configuracion nueva = new Configuracion();
                    nueva.setClave(CLAVE_NOMBRE_TALLER);
                    return nueva;
                });
        configuracion.setValor(nombre.trim().toUpperCase());
        return new ConfiguracionResponseDto(
                CLAVE_NOMBRE_TALLER,
                configuracionRepository.save(configuracion).getValor());
    }
}