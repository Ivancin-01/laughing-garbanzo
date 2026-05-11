package com.tfg.gestion_practicas.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.gestion_practicas.model.TutorCentro;
import com.tfg.gestion_practicas.repository.TutorCentroRepository;

@Service
public class TutorCentroService {

    @Autowired
    private TutorCentroRepository tutorCentroRepository;

    public Optional<TutorCentro> obtenerPorCorreo(String correo) {
        return tutorCentroRepository.findByUsuarioCorreo(correo);
    }

    public List<TutorCentro> buscarPorNombreCentro(String nombre) {
        return tutorCentroRepository.findByCentroNombreContainingIgnoreCase(nombre);
    }

    public TutorCentro guardar(TutorCentro tutorCentro) {
        return tutorCentroRepository.save(tutorCentro);
    }
}