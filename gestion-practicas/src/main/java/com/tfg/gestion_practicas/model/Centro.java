package com.tfg.gestion_practicas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="centros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Centro {

    @Id
    private Long id;


    private String nombre;

    private String ciudad;

}
