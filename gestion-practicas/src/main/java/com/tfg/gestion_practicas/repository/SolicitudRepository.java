package com.tfg.gestion_practicas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.gestion_practicas.model.Alumno;
import com.tfg.gestion_practicas.model.EstadoSolicitud;
import com.tfg.gestion_practicas.model.Solicitud;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findByAlumnoId(Long alumnoId);

    List<Solicitud> findByOfertaId(Long ofertaId);

    boolean existsByAlumnoIdAndOfertaId(Long alumnoId, Long ofertaId);

    List<Solicitud> findByOfertaEmpresaId(Long empresaId);

    List<Solicitud> findByAlumnoTutorId(Long tutorId);

    List<Solicitud> findByOfertaEmpresaIdAndEstado(Long empresaId, EstadoSolicitud estado);

    List<Solicitud> findByAlumno(Alumno alumno);
}
