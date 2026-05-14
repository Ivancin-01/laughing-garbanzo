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
    public String listarOfertas(@RequestParam(name = "buscar", required = false) String buscar, @RequestParam(name = "ciudad", required = false) String ciudad,
            Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Alumno alumno = alumnoRepository.findByUsuarioCorreo(principal.getName()).orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
        
        List<Oferta> ofertas = ofertaRepository.buscarOfertasAlumno(buscar, ciudad);
        List<String> ciudades = ofertaRepository.findCiudadesDisponibles();

        model.addAttribute("alumno", alumno);
        model.addAttribute("ofertas", ofertas);
        model.addAttribute("query", buscar);
        model.addAttribute("ciudadSeleccionada", ciudad);
        model.addAttribute("ciudades", ciudades);

        return "alumno/buscar-ofertas"; // Aquí va el listado de tarjetas
    }
}