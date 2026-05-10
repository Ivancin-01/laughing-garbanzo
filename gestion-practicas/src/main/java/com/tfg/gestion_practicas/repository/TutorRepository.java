package com.tfg.gestion_practicas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.gestion_practicas.model.Tutor;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {

    Optional<Tutor> findByUsuarioId(Long usuarioId);
    Optional<Tutor> findByUsuarioCorreo(String usuarioCorreo);
    List<Tutor> findByCentroEducativoIgnoreCase(String centroEducativo);
}
