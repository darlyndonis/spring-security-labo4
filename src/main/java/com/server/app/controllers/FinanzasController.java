package com.server.app.controllers;

import com.server.app.dto.finanzas.*;
import com.server.app.dto.response.Pagination;
import com.server.app.entities.Activo;
import com.server.app.entities.Inversion;
import com.server.app.entities.Portafolio;
import com.server.app.entities.User;
import com.server.app.services.ActivoService;
import com.server.app.services.InversionService;
import com.server.app.services.PortafolioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finanzas")
public class FinanzasController {

    private final PortafolioService portafolioService;
    private final ActivoService activoService;
    private final InversionService inversionService;

    public FinanzasController(PortafolioService portafolioService,
                              ActivoService activoService,
                              InversionService inversionService) {
        this.portafolioService = portafolioService;
        this.activoService = activoService;
        this.inversionService = inversionService;
    }

    @GetMapping("/portafolios")
    public ResponseEntity<Pagination<Portafolio>> listarPortafolios(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(portafolioService.listarPorUsuario(user.getId(), page, size));
    }

    @PostMapping("/portafolios")
    public ResponseEntity<Portafolio> crearPortafolio(
            Authentication authentication,
            @Valid @RequestBody PortafolioRequest request) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(portafolioService.crear(user, request));
    }

    @GetMapping("/activos")
    public ResponseEntity<Pagination<Activo>> listarActivos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(activoService.listarTodos(page, size));
    }

    @PostMapping("/inversiones")
    public ResponseEntity<Inversion> registrarInversion(
            Authentication authentication,
            @Valid @RequestBody InversionRequest request) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(inversionService.registrar(request, user.getId()));
    }

    @GetMapping("/portafolios/{id}/rendimiento")
    public ResponseEntity<RendimientoResponse> rendimiento(
            Authentication authentication,
            @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(portafolioService.calcularRendimiento(id, user.getId()));
    }
}