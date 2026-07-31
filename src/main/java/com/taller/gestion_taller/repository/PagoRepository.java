package com.taller.gestion_taller.repository;

import com.taller.gestion_taller.entity.Pago;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    Page<Pago> findByOrdenTrabajoId(Long ordenTrabajoId, Pageable pageable);

    @Query("select coalesce(sum(p.cantidadPagada), 0) from Pago p where p.ordenTrabajo.id = :ordenTrabajoId")
    BigDecimal sumarPagosPorOrden(@Param("ordenTrabajoId") Long ordenTrabajoId);
}
