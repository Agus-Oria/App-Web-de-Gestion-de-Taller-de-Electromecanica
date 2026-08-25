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

    Page<OrdenTrabajo> findByClienteUsuarioId(Long usuarioId, Pageable pageable);

    Optional<OrdenTrabajo> findByIdAndClienteUsuarioId(Long id, Long usuarioId);

    Page<OrdenTrabajo> findByClienteIdAndClienteUsuarioId(Long clienteId, Long usuarioId, Pageable pageable);

    Page<OrdenTrabajo> findByVehiculoIdAndVehiculoUsuarioId(Long vehiculoId, Long usuarioId, Pageable pageable);

    Page<OrdenTrabajo> findByClienteDniAndClienteUsuarioId(String dni, Long usuarioId, Pageable pageable);

    @Query("SELECT o FROM OrdenTrabajo o "
            + "WHERE o.cliente.usuario.id = :usuarioId "
            + "AND (LOWER(o.cliente.dni) LIKE LOWER(CONCAT('%', :texto, '%')) "
            + "OR LOWER(o.cliente.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) "
            + "OR LOWER(o.cliente.apellido) LIKE LOWER(CONCAT('%', :texto, '%')))")
    Page<OrdenTrabajo> buscarPorCliente(
            @Param("texto") String texto,
            @Param("usuarioId") Long usuarioId,
            Pageable pageable);

    @Query("SELECT o FROM OrdenTrabajo o "
            + "WHERE o.vehiculo.usuario.id = :usuarioId "
            + "AND LOWER(o.vehiculo.patente) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<OrdenTrabajo> findByVehiculoPatenteContainingIgnoreCaseAndUsuarioId(
            @Param("texto") String texto,
            @Param("usuarioId") Long usuarioId,
            Pageable pageable);

    Page<OrdenTrabajo> findByVehiculoPatenteIgnoreCaseAndVehiculoUsuarioId(
            String patente, Long usuarioId, Pageable pageable);

    @EntityGraph(attributePaths = {"cliente", "vehiculo", "detalles"})
    @Query("select o from OrdenTrabajo o where o.id = :id and o.cliente.usuario.id = :usuarioId")
    Optional<OrdenTrabajo> findWithDetallesByIdAndUsuarioId(
            @Param("id") Long id, @Param("usuarioId") Long usuarioId);
}