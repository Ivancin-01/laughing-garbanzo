package com.tfg.gestion_practicas.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.gestion_practicas.model.EstadoSolicitud;
import com.tfg.gestion_practicas.model.Oferta;
import com.tfg.gestion_practicas.model.Solicitud;
import com.tfg.gestion_practicas.repository.OfertaRepository;
import com.tfg.gestion_practicas.repository.SolicitudRepository;

@Service
public class SolicitudService {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private OfertaRepository ofertaRepository;

    // Creamos una solicitud.
    public Solicitud crearSolicitud(Solicitud solicitud) {

        // Comprobaciones básicas para evitar null.
        if (solicitud == null || solicitud.getAlumno() == null || solicitud.getOferta() == null) {
            throw new RuntimeException("Faltan datos para crear la solicitud");
        }

        // Evitamos que un alumno se apunte dos veces a la misma oferta.
        boolean existe = solicitudRepository.existsByAlumnoIdAndOfertaId(
                solicitud.getAlumno().getId(),
                solicitud.getOferta().getId());

        if (existe) {
            throw new RuntimeException("Ya has solicitado esta oferta");
        }

        // Comprobamos si la oferta existe.
        Long ofertaId = solicitud.getOferta().getId();
        Oferta oferta = ofertaRepository.findById(ofertaId).orElse(null);

        if (oferta == null) {
            throw new RuntimeException("Oferta no encontrada");
        }

        // Contamos cuántas solicitudes tiene ya la oferta.
        int totalSolicitudes = solicitudRepository.findByOfertaId(ofertaId).size();

        // Si ya no hay plazas, no se podrá solicitar.
        if (totalSolicitudes >= oferta.getPlazas()) {
            throw new RuntimeException("No hay plazas disponibles en esta oferta");
        }

        // Si no llega mensaje, ponemos uno automático para no romper la validación.
        if (solicitud.getMensaje() == null || solicitud.getMensaje().trim().isEmpty()) {
            solicitud.setMensaje("Solicitud enviada desde la plataforma");
        }

        // Se añade la fecha automáticamente y el estado inicial siempre será PENDIENTE.
        solicitud.setFechaSolicitud(LocalDate.now());
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setOferta(oferta);

        return solicitudRepository.save(solicitud);
    }

    // Obtenemos las solicitudes de un alumno.
    public List<Solicitud> obtenerPorAlumno(Long alumnoId) {
        return solicitudRepository.findByAlumnoId(alumnoId);
    }

    // Obtenemos las solicitudes de una oferta concreta.
    public List<Solicitud> obtenerPorOferta(Long ofertaId) {
        return solicitudRepository.findByOfertaId(ofertaId);
    }

    // Obtenemos las solicitudes de todas las ofertas de una empresa.
    public List<Solicitud> obtenerPorEmpresa(Long empresaId) {
        return solicitudRepository.findByOfertaEmpresaId(empresaId);
    }

    // Obtenemos las solicitudes de los alumnos de un tutor.
    public List<Solicitud> obtenerPorTutor(Long tutorId) {
        return solicitudRepository.findByAlumnoTutorId(tutorId);
    }

    // Obtenemos una solicitud por su id.
    public Solicitud obtenerPorId(Long id) {
        return solicitudRepository.findById(id).orElse(null);
    }

    // Guardamos cambios en una solicitud ya existente.
    public Solicitud guardar(Solicitud solicitud) {
        return solicitudRepository.save(solicitud);
    }
}