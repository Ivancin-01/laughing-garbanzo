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
        // 1. Verificamos si existe una sesión que se encuentre activa. 
        if (principal == null) {
            return "redirect:/login";
        }

        // 2. Obtenemos el alumno por su correo. Spring Security toma el email como dato para identificar al usuario.
        String email = principal.getName();

        // 3. Buscamos al alumno en la DB por su email.
        Alumno al = alumnoRepository.findByEmail(email).orElse(null);

        // 3.1. Si el email del login no existe en la tabla alumnos, hacemos saltar una nueva ventana.
        if (al == null) {
            return "redirect:/login?error=usuario-no-encontrado";
        }

        // 4. Cargamos las solicitudes reales del alumno encontrado.
        List <Solicitud> solicitudes = solicitudService.obtenerPorAlumno(al.getId());

        // 5. Pasamos los datos al modelo para que la vista 'dashboard.html' los pinte.
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
