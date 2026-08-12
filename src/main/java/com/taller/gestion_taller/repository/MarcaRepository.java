package com.taller.gestion_taller.repository;

import com.taller.gestion_taller.entity.Marca;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarcaRepository extends JpaRepository<Marca, Long> {

    Optional<Marca> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}
