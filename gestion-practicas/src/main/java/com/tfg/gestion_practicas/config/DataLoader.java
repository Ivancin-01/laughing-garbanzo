package com.tfg.gestion_practicas.config;

/*import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tfg.gestion_practicas.model.*;
import com.tfg.gestion_practicas.repository.*;

import java.time.LocalDate; */


// ESTA CLASE ES UNA CLASE DE PRUEBA DE DATOS (PARA OFERTAS).
// Esta clase se ejecuta automáticamente al iniciar la aplicación.
// Sirve para insertar datos de prueba (empresa y oferta) en la base de datos,
// para poder visualizar información en la aplicación sin tener que crearla manualmente.

/* @Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(OfertaRepository ofertaRepository, EmpresaRepository empresaRepository) {
        return args -> {

            // 🏢 Crear empresa
            Empresa empresa = new Empresa();
            empresa.setNombre("Tech Solutions");
            empresa.setSector("Informática");
            empresa.setCiudad("Madrid");
            empresa.setEmailContacto("info@tech.com");
            empresa.setTelefono("123456789");

            empresaRepository.save(empresa);

            // 📢 Crear oferta
            Oferta oferta = new Oferta();
            oferta.setTitulo("Prácticas Desarrollo Web");
            oferta.setDescripcion("Aprenderás Spring Boot y desarrollo web");
            oferta.setCiudad("Madrid");
            oferta.setModalidad("Presencial");
            oferta.setPlazas(3);
            oferta.setFechaPublicacion(LocalDate.now());
            oferta.setEmpresa(empresa);

            ofertaRepository.save(oferta);
        };
    }
} */