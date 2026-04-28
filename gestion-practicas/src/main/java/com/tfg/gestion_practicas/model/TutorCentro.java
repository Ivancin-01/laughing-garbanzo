package com.tfg.gestion_practicas.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="tutor_centro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorCentro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telefono;

    @Column(name = "nombre_centro")
    private String nombreCentro;


    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_tutores")
    private Tutor tutor;
}