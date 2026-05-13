document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("formCrearOferta");
    const titulo = document.getElementById("titulo");
    const descripcion = document.getElementById("descripcion");
    const especialidad = document.getElementById("especialidad");

    const horaInicio = document.getElementById("horaInicio");
    const horaFin = document.getElementById("horaFin");
    const horario = document.getElementById("horario");
    const horarioPreview = document.getElementById("horarioPreview");

    if (!form) {
        return;
    }

    function convertirHoraAMinutos(hora) {
        const partes = hora.split(":");
        return parseInt(partes[0], 10) * 60 + parseInt(partes[1], 10);
    }

    function actualizarHorario() {
        const inicio = horaInicio ? horaInicio.value : "";
        const fin = horaFin ? horaFin.value : "";

        if (!inicio || !fin) {
            if (horario) {
                horario.value = "";
            }

            if (horarioPreview) {
                horarioPreview.textContent = "Selecciona una hora de inicio y una hora de fin.";
                horarioPreview.classList.remove("horario-preview--ok", "horario-preview--error");
            }

            return false;
        }

        const minutosInicio = convertirHoraAMinutos(inicio);
        const minutosFin = convertirHoraAMinutos(fin);

        if (minutosFin <= minutosInicio) {
            if (horario) {
                horario.value = "";
            }

            if (horarioPreview) {
                horarioPreview.textContent = "La hora de fin debe ser posterior a la hora de inicio.";
                horarioPreview.classList.remove("horario-preview--ok");
                horarioPreview.classList.add("horario-preview--error");
            }

            return false;
        }

        const horarioFinal = inicio + " - " + fin;

        if (horario) {
            horario.value = horarioFinal;
        }

        if (horarioPreview) {
            horarioPreview.textContent = "Horario seleccionado: " + horarioFinal;
            horarioPreview.classList.remove("horario-preview--error");
            horarioPreview.classList.add("horario-preview--ok");
        }

        return true;
    }

    if (horaInicio && horaFin) {
        horaInicio.addEventListener("change", actualizarHorario);
        horaFin.addEventListener("change", actualizarHorario);
    }

    form.addEventListener("submit", function (event) {
        const tituloValor = titulo ? titulo.value.trim() : "";
        const descripcionValor = descripcion ? descripcion.value.trim() : "";
        const especialidadValor = especialidad ? especialidad.value.trim() : "";

        if (tituloValor.length < 5) {
            event.preventDefault();
            alert("Por favor, introduce un título más descriptivo.");
            titulo.focus();
            return;
        }

        if (!especialidadValor) {
            event.preventDefault();
            alert("Selecciona una especialidad recomendada para la oferta.");
            especialidad.focus();
            return;
        }

        if (!actualizarHorario()) {
            event.preventDefault();
            alert("Selecciona un horario válido para la oferta.");
            if (horaInicio && !horaInicio.value) {
                horaInicio.focus();
            } else if (horaFin) {
                horaFin.focus();
            }
            return;
        }

        if (descripcionValor.length < 20) {
            event.preventDefault();
            alert("La descripción debe tener al menos 20 caracteres.");
            descripcion.focus();
        }
    });
});