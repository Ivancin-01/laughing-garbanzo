package com.tfg.gestion_practicas.repository;

import com.tfg.gestion_practicas.model.TutorCentro;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorCentroRepository extends JpaRepository<TutorCentro, Long> {
     List<TutorCentro> findByCentroId(Long centroId);
}
