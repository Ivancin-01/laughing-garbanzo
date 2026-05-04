package com.tfg.gestion_practicas.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "empresas")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El CIF es obligatorio")
    @Size(max = 9)
    @Pattern(regexp = "^[A-Z][0-9]{8}$", message = "El CIF debe tener 9 dígitos")
    private String cif;

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Size(min = 2, max = 50)
    private String nombre;

    @NotBlank(message = "El sector es obligatorio")
    private String sector;

    @Size(max = 500)
    private String descripcion;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(min = 2, max = 40)
    private String ciudad;

    @Email(message = "Debe ser un formato válido de email")
    @NotBlank(message = "El email de contacto es obligatorio")
    private String emailContacto;

    @Pattern(regexp = "^[0-9]{9}", message = "El teléfono debe tener 9 dígitos")
    private String telefono;

    @Size(max = 200)
    private String web;

    @OneToMany(mappedBy = "empresa") // Una empresa puede publicar una o muchas ofertas.
    private List<Oferta> ofertas;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
