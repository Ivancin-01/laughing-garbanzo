package com.tfg.gestion_practicas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.gestion_practicas.model.Solicitud;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByAlumnoId(Long alumnoId);

    List<Solicitud> findByOfertaId(Long ofertaId);

    boolean existsByAlumnoIdAndOfertaId(Long alumnoId, Long ofertaId); // Este método nos va a evitar que un alumno se apunte dos veces a la misma oferta. 

    List<Solicitud> findByOfertaEmpresaId(Long empresaId); // Con esta query una empresa podrá ver las solicitudes que se realizan a todas SUS ofertas.

    List<Solicitud> findByAlumnoTutorId(Long tutorId); // Un tutor podrá ver las solicitudes realizadas por TODOS sus alumnos.
}
