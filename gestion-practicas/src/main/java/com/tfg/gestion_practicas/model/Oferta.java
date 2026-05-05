package com.tfg.gestion_practicas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ofertas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Oferta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String titulo;

    @NotBlank
    @Size(min = 20, max = 500)
    private String descripcion;

    @NotBlank
    @Size(min = 2, max = 40)
    private String ciudad;

    @NotBlank
    private String modalidad;

    // Añadimos este campo para que coincida con tu formulario
    private String especialidad;

    @Min(value = 1, message = "Debe haber como mínimo una plaza vacante")
    private Integer plazas;

    private LocalDate fechaPublicacion;

    @ManyToOne
    private Empresa empresa;

    @OneToMany(mappedBy = "oferta", cascade = CascadeType.ALL)
    private List<Solicitud> solicitudes = new ArrayList<>(); // Inicializada para evitar errores en el dashboard
}