package com.server.app.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "portafolios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Portafolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "balance_total", nullable = false)
    private Double balanceTotal = 0.0;

    @Column(name = "riesgo_perfil", nullable = false)
    private String riesgoPerfil;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;
}