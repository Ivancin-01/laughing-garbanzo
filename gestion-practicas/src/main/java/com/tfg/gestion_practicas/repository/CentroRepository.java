package com.tfg.gestion_practicas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.gestion_practicas.model.Centro;

public interface CentroRepository extends JpaRepository<Centro, Long> {

    List<Centro> findAllByOrderByNombreAsc();
}
