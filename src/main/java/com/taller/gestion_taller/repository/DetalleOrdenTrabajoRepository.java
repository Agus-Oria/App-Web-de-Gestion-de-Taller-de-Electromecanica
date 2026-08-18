package com.taller.gestion_taller.repository;

import com.taller.gestion_taller.entity.DetalleOrdenTrabajo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleOrdenTrabajoRepository extends JpaRepository<DetalleOrdenTrabajo, Long> {

    Page<DetalleOrdenTrabajo> findByOrdenTrabajoId(Long ordenTrabajoId, Pageable pageable);

    Page<DetalleOrdenTrabajo> findByOrdenTrabajoClienteUsuarioId(Long usuarioId, Pageable pageable);

    Optional<DetalleOrdenTrabajo> findByIdAndOrdenTrabajoClienteUsuarioId(Long id, Long usuarioId);
}