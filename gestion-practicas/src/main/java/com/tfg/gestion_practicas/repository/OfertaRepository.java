package com.tfg.gestion_practicas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tfg.gestion_practicas.model.Oferta;

public interface OfertaRepository extends JpaRepository<Oferta, Long> {

    List<Oferta> findByCiudad(String ciudad);

    List<Oferta> findByEmpresaId(Long empresaId);

    List<Oferta> findByTituloContainingIgnoreCaseOrEmpresaNombreContainingIgnoreCase(
            String titulo,
            String nombreEmpresa
    );

    @Query("""
        SELECT o FROM Oferta o
        WHERE
            (
                :buscar IS NULL OR :buscar = '' OR
                LOWER(o.titulo) LIKE LOWER(CONCAT('%', :buscar, '%')) OR
                LOWER(o.empresa.nombre) LIKE LOWER(CONCAT('%', :buscar, '%')) OR
                LOWER(o.especialidad) LIKE LOWER(CONCAT('%', :buscar, '%'))
            )
            AND
            (
                :ciudad IS NULL OR :ciudad = '' OR
                LOWER(o.ciudad) = LOWER(:ciudad)
            )
        ORDER BY o.fechaPublicacion DESC
    """)
    List<Oferta> buscarOfertasAlumno(
            @Param("buscar") String buscar,
            @Param("ciudad") String ciudad
    );

    @Query("""
        SELECT DISTINCT o.ciudad
        FROM Oferta o
        WHERE o.ciudad IS NOT NULL
          AND o.ciudad <> ''
        ORDER BY o.ciudad
    """)
    List<String> findCiudadesDisponibles();
}