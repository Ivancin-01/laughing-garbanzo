package com.tfg.gestion_practicas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tfg.gestion_practicas.model.Oferta;
import com.tfg.gestion_practicas.services.OfertaService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/ofertas")
public class OfertaController {
    @Autowired
    private OfertaService ofertaService; 

    // Mostramos todas las ofertas disponibles en la web.
    @GetMapping
    public String listarOfertas(Model m) {
        // Obtenemos todas las ofertas desde nuestro servicio.
        List<Oferta> ofertas = ofertaService.listarTodas();

        // Enviamos las ofertas a la vista (HTML).
        m.addAttribute("ofertas", ofertas);

        // Devolvemos la vista "ofertas/lista.html".
        return "ofertas/lista";
    }

    // Ver en detalle una oferta concreta.
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model m) {
        // Buscamos la oferta por su id.
        Oferta oferta = ofertaService.obtenerPorId(id);

        // Si no existe la oferta, redirigimos al listado. 
        if(oferta == null) {
            return "redirect:/ofertas";
        }

        // Enviamos la oferta a la vista.
        m.addAttribute("oferta", oferta);

        return "ofertas/detalle";
    }

}
