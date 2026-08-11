package com.taller.gestion_taller.repository;

import com.taller.gestion_taller.entity.OrdenTrabajo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long> {

    Page<OrdenTrabajo> findByClienteId(Long clienteId, Pageable pageable);

    Page<OrdenTrabajo> findByVehiculoId(Long vehiculoId, Pageable pageable);

    Page<OrdenTrabajo> findByClienteDni(String dni, Pageable pageable);

    Page<OrdenTrabajo> findByVehiculoPatenteIgnoreCase(String patente, Pageable pageable);

    @EntityGraph(attributePaths = {"cliente", "vehiculo", "detalles"})
    @Query("select o from OrdenTrabajo o where o.id = :id")
    Optional<OrdenTrabajo> findWithDetallesById(@Param("id") Long id);
}
