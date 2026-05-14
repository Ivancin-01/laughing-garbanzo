document.addEventListener("DOMContentLoaded", function () {
    const btnSidebar = document.getElementById("btn-toggle-sidebar");
    const sidebar = document.querySelector(".sidebar");

    const perfilTrigger = document.querySelector(".perfil-trigger");
    const perfilBoton = document.getElementById("perfil-boton");
    const dropdown = document.getElementById("dropdown-perfil");

    // =====================================================
    // SIDEBAR
    // =====================================================
    if (btnSidebar && sidebar) {
        btnSidebar.addEventListener("click", function () {
            sidebar.classList.toggle("oculto");
        });
    }

    // =====================================================
    // DROPDOWN PERFIL
    // =====================================================
    if (perfilBoton && dropdown && perfilTrigger) {
        perfilBoton.addEventListener("click", function (event) {
            event.stopPropagation();
            dropdown.classList.toggle("show");
            perfilTrigger.classList.toggle("dropdown-abierto");
        });

        window.addEventListener("click", function (event) {
            if (!perfilTrigger.contains(event.target)) {
                dropdown.classList.remove("show");
                perfilTrigger.classList.remove("dropdown-abierto");
            }
        });

        window.addEventListener("keydown", function (event) {
            if (event.key === "Escape") {
                dropdown.classList.remove("show");
                perfilTrigger.classList.remove("dropdown-abierto");
            }
        });
    }

    // =====================================================
    // EDITAR OFERTA — HORARIO Y VALIDACIÓN DE PLAZAS
    // =====================================================
    const formEditarOferta = document.querySelector(".oferta-form");
    const horaInicio = document.getElementById("horaInicio");
    const horaFin = document.getElementById("horaFin");
    const horario = document.getElementById("horario");
    const horarioPreview = document.getElementById("horarioPreview");
    const plazasInput = document.getElementById("plazas");

    function convertirHoraAMinutos(hora) {
        const partes = hora.split(":");
        return parseInt(partes[0], 10) * 60 + parseInt(partes[1], 10);
    }

    function cargarHorarioInicial() {
        if (!horario || !horaInicio || !horaFin) {
            return;
        }

        const valorActual = horario.value;

        if (!valorActual || !valorActual.includes("-")) {
            return;
        }

        const partes = valorActual.split("-");

        if (partes.length !== 2) {
            return;
        }

        const inicio = partes[0].trim();
        const fin = partes[1].trim();

        horaInicio.value = inicio;
        horaFin.value = fin;

        actualizarHorario();
    }

    function actualizarHorario() {
        if (!horaInicio || !horaFin || !horario) {
            return true;
        }

        const inicio = horaInicio.value;
        const fin = horaFin.value;

        if (!inicio && !fin) {
            if (horarioPreview) {
                const valorActual = horario.value;

                if (valorActual && valorActual.trim() !== "") {
                    horarioPreview.textContent = "Horario actual: " + valorActual;
                    horarioPreview.classList.remove("horario-preview--error");
                    horarioPreview.classList.add("horario-preview--ok");
                } else {
                    horarioPreview.textContent = "Selecciona una hora de inicio y una hora de fin.";
                    horarioPreview.classList.remove("horario-preview--ok", "horario-preview--error");
                }
            }

            return true;
        }

        if (!inicio || !fin) {
            if (horarioPreview) {
                horarioPreview.textContent = "Selecciona ambas horas para guardar el horario.";
                horarioPreview.classList.remove("horario-preview--ok");
                horarioPreview.classList.add("horario-preview--error");
            }

            return false;
        }

        if (convertirHoraAMinutos(fin) <= convertirHoraAMinutos(inicio)) {
            if (horarioPreview) {
                horarioPreview.textContent = "La hora de fin debe ser posterior a la hora de inicio.";
                horarioPreview.classList.remove("horario-preview--ok");
                horarioPreview.classList.add("horario-preview--error");
            }

            return false;
        }

        const horarioFinal = inicio + " - " + fin;
        horario.value = horarioFinal;

        if (horarioPreview) {
            horarioPreview.textContent = "Horario seleccionado: " + horarioFinal;
            horarioPreview.classList.remove("horario-preview--error");
            horarioPreview.classList.add("horario-preview--ok");
        }

        return true;
    }

    function validarPlazas() {
        if (!plazasInput) {
            return true;
        }

        const plazas = parseInt(plazasInput.value, 10);
        const minAceptadas = parseInt(plazasInput.dataset.minAceptadas || "0", 10);

        if (Number.isNaN(plazas) || plazas < 1) {
            alert("El número de plazas debe ser igual o superior a 1.");
            plazasInput.focus();
            return false;
        }

        if (plazas < minAceptadas) {
            alert("No puedes poner menos plazas que solicitudes aceptadas. Mínimo permitido: " + minAceptadas + ".");
            plazasInput.focus();
            return false;
        }

        return true;
    }

    if (horaInicio && horaFin) {
        cargarHorarioInicial();

        horaInicio.addEventListener("change", actualizarHorario);
        horaFin.addEventListener("change", actualizarHorario);
    }

    if (formEditarOferta) {
        formEditarOferta.addEventListener("submit", function (event) {
            if (!validarPlazas()) {
                event.preventDefault();
                return;
            }

            if (!actualizarHorario()) {
                event.preventDefault();
            }
        });
    }
});