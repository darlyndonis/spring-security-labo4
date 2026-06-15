package com.server.app.dto.finanzas;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RendimientoResponse {
    private Long portafolioId;
    private String nombrePortafolio;
    private Double inversionTotal;
    private Double valorMercadoActual;
    private Double gananciaAbsoluta;
    private Double rentabilidadPorcentaje;
}