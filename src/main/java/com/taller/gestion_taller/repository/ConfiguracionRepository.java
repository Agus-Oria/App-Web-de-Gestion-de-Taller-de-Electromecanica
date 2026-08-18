package com.taller.gestion_taller.repository;

import com.taller.gestion_taller.entity.Configuracion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracionRepository extends JpaRepository<Configuracion, Long> {

    Optional<Configuracion> findByClave(String clave);

    boolean existsByClave(String clave);
}