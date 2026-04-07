package com.tfg.gestion_practicas.repository;

import com.tfg.gestion_practicas.model.Centro;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CentroRepository extends JpaRepository<Centro, Long> {
    List<Centro> findByCiudad(String ciudad);
}