package com.tfg.gestion_practicas.controller;

import com.tfg.gestion_practicas.repository.TareaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TareaController {

    @Autowired
    private TareaRepository tareaRepository;

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("mensaje", "¡Conexión con Supabase con éxito!");
        model.addAttribute("totalTareas", tareaRepository.count());
        return "index"; // Esto buscará un archivo index.html en templates
    }
}
