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
        if (principal == null) return "redirect:/login";
 
        String email = principal.getName();
        Tutor tutor = tutorRepository.findByUsuarioCorreo(email).orElse(null);
 
        if (tutor == null) return "redirect:/login?error=tutor-no-encontrado";
 
        List<Alumno> misAlumnos = alumnoRepository.findByTutorId(tutor.getId());
 
        long totalAlumnos = misAlumnos.size();
        long enPracticas = misAlumnos.stream()
                .filter(a -> "EN_PRACTICAS".equals(a.getEstadoFct()))
                .count();
 
        model.addAttribute("tutor", tutor.getUsuario());
        model.addAttribute("alumnos", misAlumnos);
        model.addAttribute("totalAlumnos", totalAlumnos);
        model.addAttribute("enPracticas", enPracticas);
        model.addAttribute("pendientesRevision", totalAlumnos - enPracticas);
 
        return "tutor/dashboard";
    }
 
    
    @GetMapping("/tutor/alumnos")
    public String listarAlumnos(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
 
        Tutor tutor = tutorRepository.findByUsuarioCorreo(principal.getName()).orElse(null);
        if (tutor == null) return "redirect:/login?error=tutor-no-encontrado";
 
        List<Alumno> misAlumnos = alumnoRepository.findByTutorId(tutor.getId());
 
        model.addAttribute("tutor", tutor.getUsuario());
        model.addAttribute("alumnos", misAlumnos);
 
        return "tutor/alumnos";
    }
}