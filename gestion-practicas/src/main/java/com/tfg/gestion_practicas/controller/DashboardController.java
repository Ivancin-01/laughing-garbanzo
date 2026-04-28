package com.tfg.gestion_practicas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboardAlumno")
    public String landing() {
        return "dashboard"; // carga templates/index.html
    }
}