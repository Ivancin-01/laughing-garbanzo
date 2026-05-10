package com.tfg.gestion_practicas.dto;

import com.tfg.gestion_practicas.model.Tutor;

public class TutorCentroTutorDTO {

    private Tutor tutor;
    private long alumnosAsignados;
    private boolean disponible;

    public TutorCentroTutorDTO(Tutor tutor, long alumnosAsignados) {
        this.tutor = tutor;
        this.alumnosAsignados = alumnosAsignados;
        this.disponible = alumnosAsignados < 25;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public long getAlumnosAsignados() {
        return alumnosAsignados;
    }

    public boolean isDisponible() {
        return disponible;
    }
}
