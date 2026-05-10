package com.tfg.gestion_practicas.model;

import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "alumnos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alumno {

    @Id
    @Column(name = "usuario_id") // Ajustado: ahora el ID en la DB se llama usuario_id
    private Long id;

    @NotBlank
    private String dni;

    @NotBlank
    private String matricula;

    @ManyToOne
    @JoinColumn(name = "centro_educativo")
    private Centro centro;


    private String ciudad;
    private String cvUrl;

    @Builder.Default
    @Column(name = "estado_fct") // Es buena práctica mapear nombres con snake_case de la DB
    private String estadoFct = "PENDIENTE";

    private Integer horasFct;
    private String empresaFct;

    @Builder.Default
    @Column(name="perfil_visible")
    private Boolean perfilVisible = true;

    @Builder.Default
    @Column(name="notificaciones_email")
    private Boolean notificacionesEmail = true;

    // Configuración 1:1 Sincronizada
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @MapsId // El ID de Alumno será el mismo ID de Usuario
    @JoinColumn(name = "usuario_id") // Coincide con la PK de la tabla alumnos
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;

    @OneToMany(mappedBy = "alumno")
    private List<Solicitud> solicitudes;
}