package com.tfg.gestion_practicas.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.gestion_practicas.model.TutorCentro;

@Repository
public interface TutorCentroRepository extends JpaRepository<TutorCentro, Long> {

    Optional<TutorCentro> findByUsuarioId(Long usuarioId);
    Optional<TutorCentro> findByUsuarioCorreo(String usuarioCorreo);
    
    // Búsqueda por el nuevo campo String
    List<TutorCentro> findByNombreCentroContainingIgnoreCase(String nombreCentro);
}