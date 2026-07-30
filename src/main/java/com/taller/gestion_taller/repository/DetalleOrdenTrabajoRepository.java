package com.taller.gestion_taller.repository;

import com.taller.gestion_taller.entity.DetalleOrdenTrabajo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleOrdenTrabajoRepository extends JpaRepository<DetalleOrdenTrabajo, Long> {

    Page<DetalleOrdenTrabajo> findByOrdenTrabajoId(Long ordenTrabajoId, Pageable pageable);
}
