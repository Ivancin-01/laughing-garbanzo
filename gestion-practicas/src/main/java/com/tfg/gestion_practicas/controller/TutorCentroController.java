package com.tfg.gestion_practicas.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tfg.gestion_practicas.model.TutorCentro;
import com.tfg.gestion_practicas.repository.TutorCentroRepository;

@Controller
public class TutorCentroController {

    @Autowired
    private TutorCentroRepository tutorCentroRepository;

    @GetMapping("/tutor_centro/dashboard")
    public String dashboardTutorCentro(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        TutorCentro tutorCentro = obtenerTutorCentroLogueado(principal);

        model.addAttribute("tutorCentro", tutorCentro.getUsuario());
        model.addAttribute("nombreCentro", tutorCentro.getNombreCentro());
        model.addAttribute("tutorDocente", tutorCentro.getTutor());

        /*
         * Datos temporales para que el dashboard no rompa.
         * Más adelante los conectamos con AlumnoRepository, TutorRepository y EmpresaRepository.
         */
        model.addAttribute("totalTutores", 0);
        model.addAttribute("totalAlumnos", 0);
        model.addAttribute("alumnosPracticas", 0);
        model.addAttribute("totalEmpresas", 0);

        model.addAttribute("alumnosInformatica", 0);
        model.addAttribute("alumnosAdministracion", 0);
        model.addAttribute("alumnosOtrasAreas", 0);

        return "tutor_centro/dashboard";
    }

    @GetMapping("/tutor_centro/perfil")
    public String perfilTutorCentro(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        TutorCentro tutorCentro = obtenerTutorCentroLogueado(principal);

        model.addAttribute("tutorCentro", tutorCentro.getUsuario());
        model.addAttribute("datosTutorCentro", tutorCentro);

        return "tutor_centro/perfil";
    }

    @GetMapping("/tutor_centro/configuracion")
    public String configuracionTutorCentro(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        TutorCentro tutorCentro = obtenerTutorCentroLogueado(principal);

        model.addAttribute("tutorCentro", tutorCentro.getUsuario());
        model.addAttribute("datosTutorCentro", tutorCentro);

        return "tutor_centro/configuracion";
    }

    private TutorCentro obtenerTutorCentroLogueado(Principal principal) {
        return tutorCentroRepository.findByUsuarioCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("Tutor de centro no encontrado"));
    }
}