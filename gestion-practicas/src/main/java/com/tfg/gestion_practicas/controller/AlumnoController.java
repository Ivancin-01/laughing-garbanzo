package com.tfg.gestion_practicas.controller;

import com.tfg.gestion_practicas.services.AlumnoService;
import java.util.List;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tfg.gestion_practicas.model.Alumno;
import com.tfg.gestion_practicas.model.Solicitud;
import com.tfg.gestion_practicas.repository.AlumnoRepository;
import com.tfg.gestion_practicas.services.SolicitudService;

@Controller
public class AlumnoController {
    private final AuthController authController;

    private final AlumnoService alumnoService;

    @Autowired
    private SolicitudService solicitudService;

    @Autowired
    private AlumnoRepository alumnoRepository;

    AlumnoController(AlumnoService alumnoService, AuthController authController) {
        this.alumnoService = alumnoService;
        this.authController = authController;
    }

    // Dashboard de alumno simplificada - para trabajar mejor.
    @GetMapping("/alumno/dashboard")
    public String dashboardAlumno (Model model, Principal principal) {
        // 1. Obtenemos el email del usuario que inicie sesión.
        String email = principal.getName();

        // 2. Buscamos al alumno por su email.
        Alumno al = alumnoRepository.findByEmail(email).orElse(null);

        if (al == null) {
            return "redirect:/login?error=usuario-no-encontrado";
        }

        // 3. Cargamos las solicitudes reales. 
        List<Solicitud> solicitudes = solicitudService.obtenerPorAlumno(al.getId());

        model.addAttribute("alumno", al);
        model.addAttribute("solicitudes", solicitudes);

        return "alumno/dashboard";
    }

    // Página completa donde nos aparecen todas las solicitudes realizadas por un alumno.
    @GetMapping("/alumno/solicitudes")
    public String verSolicitudesAlumno(Model model) {
        Alumno al = alumnoRepository.findById(1L).orElse(null);

        if (al == null) {
            return "redirect:/ofertas?error=alumno-no-existe";
        }

        List<Solicitud> solicitudes = solicitudService.obtenerPorAlumno(al.getId());

        model.addAttribute("alumno", al);
        model.addAttribute("solicitudes", solicitudes);

        return "alumno/solicitudes";
    }
}
