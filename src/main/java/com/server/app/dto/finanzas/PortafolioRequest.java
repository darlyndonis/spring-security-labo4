package com.server.app.dto.finanzas;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PortafolioRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "El perfil de riesgo no puede estar vacío")
    @Pattern(regexp = "BAJO|MEDIO|ALTO", message = "El perfil de riesgo debe ser BAJO, MEDIO o ALTO")
    private String riesgoPerfil;
}