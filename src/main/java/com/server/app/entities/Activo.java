package com.server.app.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "activos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String simbolo;

    @Column(nullable = false)
    private String mercado;

    @Column(name = "precio_mercado", nullable = false)
    private Double precioMercado;

    @Column(nullable = false)
    private String sector;
}