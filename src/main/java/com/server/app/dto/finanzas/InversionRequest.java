package com.server.app.dto.finanzas;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class InversionRequest {

    @NotNull(message = "La cantidad no puede ser nula")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotNull(message = "El precio de compra no puede ser nulo")
    @Positive(message = "El precio de compra debe ser positivo")
    private Double precioCompra;

    @NotNull(message = "El portafolio es requerido")
    private Long portafolioId;

    @NotNull(message = "El activo es requerido")
    private Long activoId;

    @NotBlank(message = "El estado no puede estar vacío")
    @Pattern(regexp = "ABIERTA|CERRADA", message = "El estado debe ser ABIERTA o CERRADA")
    private String estado;
}