package com.taller.gestion_taller.repository;

import com.taller.gestion_taller.entity.Cliente;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Page<Cliente> findAllByUsuarioId(Long usuarioId, Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE c.usuario.id = :usuarioId "
            + "AND (LOWER(c.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) "
            + "OR LOWER(c.apellido) LIKE LOWER(CONCAT('%', :texto, '%')) "
            + "OR LOWER(c.dni) LIKE LOWER(CONCAT('%', :texto, '%')))")
    Page<Cliente> buscarPorNombreOApellido(
            @Param("texto") String texto,
            @Param("usuarioId") Long usuarioId,
            Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE c.usuario.id = :usuarioId "
            + "AND LOWER(c.dni) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Cliente> buscarPorDniContainingIgnoreCase(
            @Param("texto") String texto,
            @Param("usuarioId") Long usuarioId,
            Pageable pageable);

    Optional<Cliente> findByDniAndUsuarioId(String dni, Long usuarioId);

    Optional<Cliente> findByIdAndUsuarioId(Long id, Long usuarioId);

    boolean existsByDniAndUsuarioId(String dni, Long usuarioId);

    boolean existsByDniAndIdNotAndUsuarioId(String dni, Long id, Long usuarioId);
}