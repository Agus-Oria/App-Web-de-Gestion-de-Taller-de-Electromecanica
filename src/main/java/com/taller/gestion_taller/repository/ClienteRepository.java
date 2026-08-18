package com.taller.gestion_taller.repository;

import com.taller.gestion_taller.entity.Cliente;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Page<Cliente> findAllByUsuarioId(Long usuarioId, Pageable pageable);

    Page<Cliente> findByNombreContainingIgnoreCaseAndUsuarioId(String nombre, Long usuarioId, Pageable pageable);

    Optional<Cliente> findByDniAndUsuarioId(String dni, Long usuarioId);

    Optional<Cliente> findByIdAndUsuarioId(Long id, Long usuarioId);

    boolean existsByDniAndUsuarioId(String dni, Long usuarioId);

    boolean existsByDniAndIdNotAndUsuarioId(String dni, Long id, Long usuarioId);
}