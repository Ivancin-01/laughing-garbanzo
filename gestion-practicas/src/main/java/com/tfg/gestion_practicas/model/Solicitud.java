package com.tfg.gestion_practicas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "solicitudes")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaSolicitud;

    @NotBlank
    @Size(min = 10, max = 300)
    private String mensaje;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado;

    @Builder.Default
    @NotNull
    @Enumerated(EnumType.STRING)
    private EstadoPractica estadoPractica = EstadoPractica.PENDIENTE_INICIO;

    @ManyToOne
    @JoinColumn(name = "oferta_id") // Especificamos el nombre exacto en DB
    private Oferta oferta;

    @ManyToOne
    @JoinColumn(name = "alumno_id", referencedColumnName = "usuario_id")
    private Alumno alumno;
}
