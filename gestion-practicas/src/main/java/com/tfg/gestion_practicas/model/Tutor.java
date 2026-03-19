package com.tfg.gestion_practicas.model;

import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="tutores")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Tutor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String departamento;

    private String centroEducativo;

    private String telefono;

    @OneToOne
    private Usuario usuario;

    @OneToMany(mappedBy = "tutor")
    private List<Alumno> alumnos;
}
