package com.tfg.gestion_practicas.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.gestion_practicas.model.Oferta;
import com.tfg.gestion_practicas.repository.OfertaRepository;

@Service
public class OfertaService {
    @Autowired
    private OfertaRepository ofertaRepository;


    // Devuelve todas las ofertas existentes en la base de datos.
    public List<Oferta> listarTodas() {
        return ofertaRepository.findAll();
    }

    // Creamos una oferta y se le añade automáticamente la fecha de publicación.
    public Oferta crear(Oferta oferta) {
        oferta.setFechaPublicacion(LocalDate.now());
        return ofertaRepository.save(oferta);
    }

    // Devuelve todas las empresas asignadas a una empresa concreta (Filtrado).
    public List<Oferta> obtenerPorEmpresa(Long empresaId) {
        return ofertaRepository.findByEmpresaId(empresaId);
    }
}
