package com.tfg.gestion_practicas.controller;

import com.tfg.gestion_practicas.model.Centro;
import com.tfg.gestion_practicas.services.CentroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/centros")
public class CentroController {

    @Autowired
    private CentroService centroService;

    // Listar centros (opcional, por si quieres ver los que hay)
    @GetMapping
    public String listarCentros(Model model) {
        model.addAttribute("centros", centroService.listarTodos());
        return "lista-centros"; // Necesitarías un HTML llamado lista-centros.html
    }

    // Mostrar formulario para crear un centro nuevo
    @GetMapping("/nuevo")
    public String formularioCentro() {
        return "form-centro"; // Necesitarías un HTML llamado form-centro.html
    }

    // Guardar el centro enviado desde el formulario
    @PostMapping("/guardar")
    public String guardarCentro(Centro centro) {
        centroService.guardar(centro);
        return "redirect:/centros?exito";
    }
}
