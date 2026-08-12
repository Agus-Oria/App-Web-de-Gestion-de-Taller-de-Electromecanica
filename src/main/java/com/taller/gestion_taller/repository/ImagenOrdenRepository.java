package com.taller.gestion_taller.repository;

import com.taller.gestion_taller.entity.ImagenOrden;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagenOrdenRepository extends JpaRepository<ImagenOrden, Long> {

    List<ImagenOrden> findByOrdenTrabajoIdOrderByIdAsc(Long ordenTrabajoId);

    long countByOrdenTrabajoId(Long ordenTrabajoId);
}