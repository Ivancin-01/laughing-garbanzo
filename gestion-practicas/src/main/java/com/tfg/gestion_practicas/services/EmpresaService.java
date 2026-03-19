package com.tfg.gestion_practicas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.gestion_practicas.model.Solicitud;
import com.tfg.gestion_practicas.repository.SolicitudRepository;

@Service
public class EmpresaService {
    @Autowired
    private SolicitudRepository solicitudRepository;

    // Nos permite ver todas las solicitudes que han llegado a una empresa.
    public List<Solicitud> verSolicitudes(Long empresaId) {
        return solicitudRepository.findByOfertaEmpresaId(empresaId);
    }
}
