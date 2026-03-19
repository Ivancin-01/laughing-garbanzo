package com.tfg.gestion_practicas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.gestion_practicas.model.Alumno;
import com.tfg.gestion_practicas.repository.AlumnoRepository;

@Service
public class TutorService {
    @Autowired
    private AlumnoRepository alumnoRepository;


    // Devuelve todos los alumnos asignados a un tutor concreto.
    public List<Alumno> obtenerAlumnos(Long tutorId) {
        return alumnoRepository.findByTutorId(tutorId);
    }
}
