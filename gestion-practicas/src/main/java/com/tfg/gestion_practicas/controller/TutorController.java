package com.tfg.gestion_practicas.controller;

import com.tfg.gestion_practicas.model.Alumno;
import com.tfg.gestion_practicas.model.Tutor;
import com.tfg.gestion_practicas.repository.AlumnoRepository;
import com.tfg.gestion_practicas.repository.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class TutorController {

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @GetMapping("/tutor/dashboard")
    public String dashboardTutor(Model model, Principal principal) {
        // 1. Verificamos sesión (igual que hizo Iván)
        if (principal == null) return "redirect:/login";

        // 2. Buscamos los datos del Tutor logueado por su email
        String email = principal.getName();
        Tutor tutor = tutorRepository.findByUsuarioCorreo(email).orElse(null);

        if (tutor == null) {
            return "redirect:/login?error=tutor-no-encontrado";
        }

        // 3. Obtenemos los alumnos que tiene asignados este tutor
        // Nota: Tendrás que crear este método en AlumnoRepository
        List<Alumno> misAlumnos = alumnoRepository.findByTutorId(tutor.getId());

        // 4. Cálculos para las estadísticas (stats) del dashboard
        long totalAlumnos = misAlumnos.size();
        long enPracticas = misAlumnos.stream()
                .filter(a -> "EN_PRACTICAS".equals(a.getEstadoFct()))
                .count();

        // 5. Pasamos todo al HTML que creamos antes
        model.addAttribute("tutor", tutor.getUsuario()); // Para el nombre y avatar
        model.addAttribute("alumnos", misAlumnos);
        model.addAttribute("totalAlumnos", totalAlumnos);
        model.addAttribute("enPracticas", enPracticas);
        model.addAttribute("pendientesRevision", totalAlumnos - enPracticas); // Ejemplo

        return "tutor/dashboard"; // Ruta de tu carpeta templates
    }

    @GetMapping("/tutor/alumnos")
    public String listarAlumnos(Model model, Principal principal) {
        // Aquí irá la lógica para la página detallada de alumnos
        return "tutor/alumnos";
    }
}