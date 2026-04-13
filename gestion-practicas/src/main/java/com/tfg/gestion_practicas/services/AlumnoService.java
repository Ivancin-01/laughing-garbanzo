package com.tfg.gestion_practicas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.gestion_practicas.model.Alumno;
import com.tfg.gestion_practicas.repository.AlumnoRepository;

@Service
public class AlumnoService {
    @Autowired
    private AlumnoRepository alumnoRepository;

    // Buscamos un alumno a partir del ID del usuario. 
    public Alumno obtenerPorUsuario(Long usuarioId) {
      Alumno alumno = alumnoRepository.findByUsuarioId(usuarioId).orElse(null);

      // Si no existe, lanzamos un error sencillo. 
      if (alumno == null) {
        throw new RuntimeException("Alumno no encontrado por el ID de usuario.");
      }

      return alumno;
    }

    // Nuevo método: Buscar por email un usuario. 
    public Alumno buscarPorEmail(String email) {
      return alumnoRepository.findByUsuarioCorreo(email).orElseThrow(() -> new RuntimeException("Alumno no encontrado con email: " + email));
    }

    public void guardar(Alumno alumno) {
        alumnoRepository.save(alumno);
    }
}
