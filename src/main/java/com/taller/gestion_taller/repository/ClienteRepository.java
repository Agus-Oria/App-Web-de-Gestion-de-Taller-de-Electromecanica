package com.taller.gestion_taller.repository;

import com.taller.gestion_taller.entity.Cliente;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Page<Cliente> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    Optional<Cliente> findByDni(String dni);

    boolean existsByDni(String dni);

    boolean existsByDniAndIdNot(String dni, Long id);
}
