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

    private String centroEducativo;

    @Column(name = "especialidad")
    private String especialidad;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "departamento")
    private String departamento;

    @Column(name = "notificaciones_email")
    private Boolean notificacionesEmail = true;

    @OneToOne
    private Usuario usuario;

    @OneToMany(mappedBy = "tutor")
    private List<Alumno> alumnos;
}
