package com.tfg.gestion_practicas.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tfg.gestion_practicas.model.Empresa;
import com.tfg.gestion_practicas.model.Solicitud;
import com.tfg.gestion_practicas.model.Usuario;
import com.tfg.gestion_practicas.repository.EmpresaRepository;
import com.tfg.gestion_practicas.repository.SolicitudRepository;

import jakarta.transaction.Transactional;

@Service
public class EmpresaService {
    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    // Nos permite ver todas las solicitudes que han llegado a una empresa.
    public List<Solicitud> verSolicitudes(Long empresaId) {
        return solicitudRepository.findByOfertaEmpresaId(empresaId);
    }

    public Optional<Empresa> buscarEmpresaPorCorreoUsuario(String correo) {
        return empresaRepository.findByUsuarioCorreo(correo);
    }

    @Transactional
    public boolean cambiarPassword (String correoUsuario, String passwordActual, String passwordNueva, String passwordConfirmacion) {
        Optional<Empresa> empresaOpt = empresaRepository.findByUsuarioCorreo(correoUsuario);

        if (empresaOpt.isEmpty()) {
            return false;
        }

        Empresa empresa = empresaOpt.get();
        Usuario usuario = empresa.getUsuario();

        if(usuario == null) {
            return false;
        }

        if (passwordActual == null || passwordNueva == null || passwordConfirmacion == null) {
            return false;
        }

        if(!passwordNueva.equals(passwordConfirmacion)) {
            return false;
        }

        if (passwordNueva.length() < 6) {
            return false;
        }

        if(!passwordEncoder.matches(passwordActual, usuario.getPwd())) {
            return false;
        }

        usuario.setPwd(passwordEncoder.encode(passwordNueva));
        return true;
    }

    @Transactional
    public boolean actualizarContacto(String correoUsuario, String emailContacto, String telefonoContacto) {
        Optional<Empresa> empresaOpt = empresaRepository.findByUsuarioCorreo(correoUsuario);

        if (empresaOpt.isEmpty()) {
            return false;
        }

        Empresa empresa = empresaOpt.get();

        if (emailContacto == null || emailContacto.isBlank()) {
            return false;
        }

        if (telefonoContacto != null && !telefonoContacto.isBlank() && !telefonoContacto.matches("^[0-9]{9}$")) {
            return false;
        }

        empresa.setEmailContacto(emailContacto);
        empresa.setTelefono(telefonoContacto);

        return true;
    }

    @Transactional
    public boolean actualizarNotificaciones(String correoUsuario, boolean notificarSolicitudes, boolean resumenSemanal) {
        Optional<Empresa> empresaOpt = empresaRepository.findByUsuarioCorreo(correoUsuario);

        if (empresaOpt.isEmpty()) {
            return false;
        }

        Empresa empresa = empresaOpt.get();

        empresa.setNotificarSolicitudes(notificarSolicitudes);
        empresa.setResumenSemanal(resumenSemanal);

        return true;
    }
}