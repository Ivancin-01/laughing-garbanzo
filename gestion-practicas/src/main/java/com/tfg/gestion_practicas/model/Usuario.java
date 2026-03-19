package com.tfg.gestion_practicas.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 50)
    @Column(unique = true,nullable = false)
    private String username;
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String apellidos;
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fNac;
    @NotBlank(message = "El nombre es obligatorio")
    @Email(message = "Formato de email invalido")
    @Column(unique = true,nullable = false)
    private String correo;
    @NotBlank
    private String pwd;
    @Column(nullable = false, updatable = false)
    private LocalDateTime fCreacion;
    @NotNull
    private boolean activo;
    @NotBlank
    @Enumerated(EnumType.STRING)
    private Rol rol;

}
