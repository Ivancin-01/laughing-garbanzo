package com.tfg.gestion_practicas.services;

import com.tfg.gestion_practicas.model.Centro;
import com.tfg.gestion_practicas.repository.CentroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CentroService {

    @Autowired
    private CentroRepository centroRepository;

    public List<Centro> listarTodos() {
        return centroRepository.findAll();
    }

    public Centro guardar(Centro centro) {
        return centroRepository.save(centro);
    }

    public Centro buscarPorId(Long id) {
        return centroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Centro no encontrado"));
    }
}