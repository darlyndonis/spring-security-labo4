package com.server.app.services;

import com.server.app.dto.finanzas.InversionRequest;
import com.server.app.entities.Activo;
import com.server.app.entities.Inversion;
import com.server.app.entities.Portafolio;
import com.server.app.exceptions.ForbiddenException;
import com.server.app.exceptions.NotFoundException;
import com.server.app.repositories.ActivoRepository;
import com.server.app.repositories.InversionRepository;
import com.server.app.repositories.PortafolioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class InversionService {

    private final InversionRepository inversionRepository;
    private final PortafolioRepository portafolioRepository;
    private final ActivoRepository activoRepository;

    @Transactional
    public Inversion registrar(InversionRequest request, int usuarioId) {
        Portafolio portafolio = portafolioRepository.findById(request.getPortafolioId())
                .orElseThrow(() -> new NotFoundException("Portafolio no encontrado"));

        if (portafolio.getUsuario().getId() != usuarioId) {
            throw new ForbiddenException("No tienes acceso a este portafolio");
        }

        Activo activo = activoRepository.findById(request.getActivoId())
                .orElseThrow(() -> new NotFoundException("Activo no encontrado"));

        Inversion inversion = Inversion.builder()
                .cantidad(request.getCantidad())
                .precioCompra(request.getPrecioCompra())
                .fecha(LocalDateTime.now())
                .estado(request.getEstado())
                .portafolio(portafolio)
                .activo(activo)
                .build();

        double costo = request.getCantidad() * request.getPrecioCompra();
        portafolio.setBalanceTotal(portafolio.getBalanceTotal() + costo);
        portafolioRepository.save(portafolio);

        return inversionRepository.save(inversion);
    }
}