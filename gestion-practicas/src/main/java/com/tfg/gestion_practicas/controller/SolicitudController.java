package com.tfg.gestion_practicas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tfg.gestion_practicas.model.Alumno;
import com.tfg.gestion_practicas.model.Oferta;
import com.tfg.gestion_practicas.model.Solicitud;
import com.tfg.gestion_practicas.repository.AlumnoRepository;
import com.tfg.gestion_practicas.services.OfertaService;
import com.tfg.gestion_practicas.services.SolicitudService;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudController {

    @Autowired
    private SolicitudService solicitudService;

    @Autowired
    private OfertaService ofertaService;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @PostMapping("/crear")
    public String crearSolicitud(@RequestParam("ofertaId") Long ofertaId) {

        // Buscar la oferta por su id
        Oferta oferta = ofertaService.obtenerPorId(ofertaId);

        if (oferta == null) {
            return "redirect:/ofertas?error=oferta-no-existe";
        }

        // Alumno de prueba temporal
        Alumno alumno = alumnoRepository.findById(1L).orElse(null);

        if (alumno == null) {
            return "redirect:/ofertas?error=alumno-no-existe";
        }

        // Crear la solicitud
        Solicitud solicitud = new Solicitud();
        solicitud.setOferta(oferta);
        solicitud.setAlumno(alumno);
        solicitud.setMensaje("Solicitud enviada desde la plataforma");

        try {
            solicitudService.crearSolicitud(solicitud);
            return "redirect:/ofertas?exito";
        } catch (RuntimeException e) {
            return "redirect:/ofertas?error";
        }
    }
}
