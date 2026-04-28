package com.tfg.gestion_practicas.controller;

import com.tfg.gestion_practicas.model.TutorCentro;
import com.tfg.gestion_practicas.repository.TutorCentroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class TutorCentroController {

    @Autowired
    private TutorCentroRepository tutorCentroRepository;

    @GetMapping("/tutor-centro/dashboard")
    public String dashboardTutorCentro(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        TutorCentro tutorCentro = tutorCentroRepository.findByUsuarioCorreo(principal.getName()).orElse(null);
        if (tutorCentro == null) return "redirect:/login?error=tutor-centro-no-encontrado";

        model.addAttribute("tutorCentro", tutorCentro.getUsuario());
        model.addAttribute("nombreCentro", tutorCentro.getNombreCentro()); // Acceso directo al String
        model.addAttribute("tutorDocente", tutorCentro.getTutor());

        return "tutor_centro/dashboard";
    }

    @GetMapping("/tutor-centro/perfil")
    public String perfilTutorCentro(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        TutorCentro tc = tutorCentroRepository.findByUsuarioCorreo(principal.getName()).orElse(null);
        if (tc == null) return "redirect:/login?error=tutor-centro-no-encontrado";

        model.addAttribute("perfil", tc);
        return "tutor_centro/perfil";
    }
}