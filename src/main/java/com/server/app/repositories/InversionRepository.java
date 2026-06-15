package com.server.app.repositories;

import com.server.app.entities.Inversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface InversionRepository extends JpaRepository<Inversion, Long> {

    @Query("SELECT i FROM Inversion i WHERE i.portafolio.id = :portafolioId AND i.estado = 'ABIERTA'")
    List<Inversion> findAbiertas(@Param("portafolioId") Long portafolioId);
}