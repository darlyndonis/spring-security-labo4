package com.server.app.repositories;

import com.server.app.entities.Activo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivoRepository extends JpaRepository<Activo, Long> {
    Page<Activo> findAll(Pageable pageable);
}