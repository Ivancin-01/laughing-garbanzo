package com.tfg.gestion_practicas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @GetMapping("/cuenta-desactivada")
    public String cuentaDesactivada() {
        return "cuenta-desactivada";
    }
    
    @GetMapping("/recuperar-password")
    public String mostrarRecuperarPassword() {
        return "recuperar-password";
    }

    @PostMapping("/recuperar-password")
    public String procesarRecuperarPassword(@RequestParam String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            return "redirect:/recuperar-password?error";
        }

        return "redirect:/recuperar-password?enviado";
    }
}
