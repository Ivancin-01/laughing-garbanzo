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

    @ManyToOne
    @MapsId
    @JoinColumn(name = "id_centro")
    private Centro centro;

    private String telefono;

    @OneToOne
    private Usuario usuario;

    @OneToMany(mappedBy = "tutor")
    private List<Alumno> alumnos;
}
