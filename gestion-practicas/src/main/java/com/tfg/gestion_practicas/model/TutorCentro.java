package com.tfg.gestion_practicas.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tutor_centro")
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

    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_centro")
    private Centro centro;

    public String getNombreCentro() {
        if (centro != null && centro.getNombre() != null) {
            return centro.getNombre();
        }

        return "Centro no asignado";
    }
}