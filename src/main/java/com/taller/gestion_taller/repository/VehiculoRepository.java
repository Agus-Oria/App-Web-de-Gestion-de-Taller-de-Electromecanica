package com.taller.gestion_taller.repository;

import com.taller.gestion_taller.entity.Vehiculo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    Page<Vehiculo> findAllByUsuarioId(Long usuarioId, Pageable pageable);

    Optional<Vehiculo> findByIdAndUsuarioId(Long id, Long usuarioId);

    Optional<Vehiculo> findByPatenteIgnoreCaseAndUsuarioId(String patente, Long usuarioId);

    boolean existsByPatenteIgnoreCaseAndUsuarioId(String patente, Long usuarioId);

    boolean existsByPatenteIgnoreCaseAndIdNotAndUsuarioId(String patente, Long id, Long usuarioId);
}