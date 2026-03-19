package com.tfg.gestion_practicas.model;

import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name="alumnos")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String dni;

    @NotBlank
    private String cursoAcademico;

    private String centroEducativo;

    private String ciudad; 

    private String cvUrl;

    @OneToOne
    private Usuario usuario;

    @ManyToOne
    private Tutor tutor;

    @OneToMany(mappedBy = "alumno")
    private List<Solicitud> solicitudes;
    
}
