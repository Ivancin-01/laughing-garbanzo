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

    @NotBlank
    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado;

    @ManyToOne
    private Oferta oferta;
}
