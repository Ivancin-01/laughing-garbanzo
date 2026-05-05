package com.tfg.gestion_practicas.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tfg.gestion_practicas.model.Alumno;
import com.tfg.gestion_practicas.model.Oferta;
import com.tfg.gestion_practicas.services.OfertaService;
import com.tfg.gestion_practicas.repository.AlumnoRepository;
import com.tfg.gestion_practicas.repository.OfertaRepository;

import org.springframework.ui.Model;

@Controller
public class OfertaController {
    @Autowired
    private OfertaService ofertaService;
    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private OfertaRepository ofertaRepository;

    // Mostramos todas las ofertas disponibles en la web.
    @GetMapping("/ofertas")
    public String listarOfertas(@RequestParam(name = "buscar", required = false) String buscar,
            Model model, Principal principal) {
        if (principal != null) {
            alumnoRepository.findByUsuarioCorreo(principal.getName())
                    .ifPresent(al -> model.addAttribute("alumno", al));
        }

        List<Oferta> ofertas = (buscar != null && !buscar.isEmpty())
                ? ofertaRepository.findByTituloContainingIgnoreCaseOrEmpresaNombreContainingIgnoreCase(buscar, buscar)
                : ofertaRepository.findAll();

        model.addAttribute("ofertas", ofertas);
        model.addAttribute("query", buscar);

        return "alumno/buscar-ofertas"; // Aquí va el listado de tarjetas
    }
}