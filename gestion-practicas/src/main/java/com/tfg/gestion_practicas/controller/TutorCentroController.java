package com.tfg.gestion_practicas.controller;

import com.tfg.gestion_practicas.model.Usuario;
import com.tfg.gestion_practicas.services.TutorCentroService;
import com.tfg.gestion_practicas.services.CentroService; // Para listar los centros en el combo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tutores")
public class TutorCentroController {

    @Autowired
    private TutorCentroService tutorCentroService;
    
    @Autowired
    private CentroService centroService;

    @GetMapping("/registro")
    public String mostrarFormulario(Model model) {
        // Pasamos la lista de centros para que el usuario pueda elegir uno en el formulario
        model.addAttribute("centros", centroService.listarTodos()); 
        return "registro-tutor"; 
    }

    @PostMapping("/registrar")
    public String registrarTutor(Usuario usuario, 
                                 @RequestParam String telefono, 
                                 @RequestParam Long idCentro) {
        try {
            tutorCentroService.registrarTutor(usuario, telefono, idCentro);
            return "redirect:/tutores/registro?exito";
        } catch (RuntimeException e) {
            // Podrías pasar el mensaje de error: ?error=nombre_de_error
            return "redirect:/tutores/registro?error";
        }
    }
}