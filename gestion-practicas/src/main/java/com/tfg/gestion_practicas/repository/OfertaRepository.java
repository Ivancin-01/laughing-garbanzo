package com.tfg.gestion_practicas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.gestion_practicas.model.Oferta;

public interface OfertaRepository extends JpaRepository<Oferta, Long> {
    List<Oferta> findByCiudad(String ciudad);
    List<Oferta> findByEmpresaId(Long empresaId); // Estos son claves para mostrar todas las ofertas, y filtrar por aquellas ofertas que sean únicas de una empresa.
}
