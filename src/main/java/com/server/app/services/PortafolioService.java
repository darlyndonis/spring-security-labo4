package com.server.app.services;

import com.server.app.dto.finanzas.PortafolioRequest;
import com.server.app.dto.finanzas.RendimientoResponse;
import com.server.app.dto.response.Pagination;
import com.server.app.dto.response.PaginationMeta;
import com.server.app.entities.Inversion;
import com.server.app.entities.Portafolio;
import com.server.app.entities.User;
import com.server.app.exceptions.ForbiddenException;
import com.server.app.exceptions.NotFoundException;
import com.server.app.repositories.InversionRepository;
import com.server.app.repositories.PortafolioRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PortafolioService {

    private final PortafolioRepository portafolioRepository;
    private final InversionRepository inversionRepository;

    @Transactional(readOnly = true)
    public Pagination<Portafolio> listarPorUsuario(int usuarioId, int page, int size) {
        Page<Portafolio> p = portafolioRepository.findByUsuarioId(usuarioId, PageRequest.of(page, size));
        return new Pagination<>(p.getContent(),
                new PaginationMeta(p.getNumber(), p.getSize(), p.getTotalPages(), p.getTotalElements()));
    }

    @Transactional
    public Portafolio crear(User usuario, PortafolioRequest request) {
        Portafolio portafolio = Portafolio.builder()
                .nombre(request.getNombre())
                .riesgoPerfil(request.getRiesgoPerfil())
                .balanceTotal(0.0)
                .usuario(usuario)
                .build();
        return portafolioRepository.save(portafolio);
    }

    @Transactional(readOnly = true)
    public RendimientoResponse calcularRendimiento(Long portafolioId, int usuarioId) {
        Portafolio portafolio = portafolioRepository.findById(portafolioId)
                .orElseThrow(() -> new NotFoundException("Portafolio no encontrado"));

        if (portafolio.getUsuario().getId() != usuarioId) {
            throw new ForbiddenException("No tienes acceso a este portafolio");
        }

        List<Inversion> inversiones = inversionRepository.findAbiertas(portafolioId);

        double inversionTotal = inversiones.stream()
                .mapToDouble(i -> i.getCantidad() * i.getPrecioCompra()).sum();
        double valorMercado = inversiones.stream()
                .mapToDouble(i -> i.getCantidad() * i.getActivo().getPrecioMercado()).sum();
        double ganancia = valorMercado - inversionTotal;
        double rentabilidad = inversionTotal == 0 ? 0 : (ganancia / inversionTotal) * 100;

        return new RendimientoResponse(portafolioId, portafolio.getNombre(),
                inversionTotal, valorMercado, ganancia, rentabilidad);
    }
}