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

    // Aquí es donde se guardará lo que elijan en el desplegable (Informatica, etc.)
    // He cambiado el nombre a 'matricula' para que coincida con el Service, 
    // pero puedes dejarlo como 'cursoAcademico' si prefieres, solo cámbialo en el Service.
    @NotBlank
    private String matricula; 

    private String centroEducativo;

    private String ciudad; 

    private String cvUrl;
    
    private String nombre;
    
    private String email;

    // Configuración correcta de la relación 1:1
    @OneToOne
    @MapsId // IMPORTANTE: Esto dice que el ID de esta tabla es el ID de la tabla Usuario
    @JoinColumn(name = "usuario_id") // Nombre de la columna en Supabase
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "tutor_id") // Es buena práctica definir el nombre de la columna FK
    private Tutor tutor;

    @OneToMany(mappedBy = "alumno")
    private List<Solicitud> solicitudes;
}