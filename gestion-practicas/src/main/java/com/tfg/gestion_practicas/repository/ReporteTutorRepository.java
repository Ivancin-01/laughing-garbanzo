package com.tfg.gestion_practicas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.gestion_practicas.model.ReporteTutor;

public interface ReporteTutorRepository extends JpaRepository<ReporteTutor, Long> {

    List<ReporteTutor> findByTutorIdOrderByFechaCreacionDesc(Long tutorId);

    List<ReporteTutor> findByAlumnoIdOrderByFechaCreacionDesc(Long alumnoId);
}
