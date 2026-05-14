document.addEventListener("DOMContentLoaded", function () {
    const btnSidebar = document.getElementById("btn-toggle-sidebar");
    const sidebar = document.querySelector(".sidebar");

    const perfilTrigger = document.querySelector(".perfil-trigger");
    const perfilBoton = document.getElementById("perfil-boton");
    const dropdown = document.getElementById("dropdown-perfil");

    const buscador = document.getElementById("buscadorAlumnosTutor");
    const filtroEstado = document.getElementById("filtroEstado");
    const filas = document.querySelectorAll(".fila-alumno");
    const sinResultados = document.getElementById("sinResultados");

    if (btnSidebar && sidebar) {
        btnSidebar.addEventListener("click", function () {
            sidebar.classList.toggle("oculto");
        });
    }

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

    function normalizarEstado(estado) {
        if (!estado) {
            return "EN_BUSQUEDA";
        }

        const limpio = estado.toUpperCase().trim();

        if (limpio === "PENDIENTE") {
            return "EN_BUSQUEDA";
        }

        return limpio;
    }

    function filtrarAlumnos() {
        const texto = buscador ? buscador.value.toLowerCase().trim() : "";
        const estadoSeleccionado = filtroEstado ? filtroEstado.value : "TODOS";

        let visibles = 0;

        filas.forEach(function (fila) {
            const nombre = (fila.dataset.nombre || "").toLowerCase();
            const email = (fila.dataset.email || "").toLowerCase();
            const estado = normalizarEstado(fila.dataset.estado || "");

            const coincideTexto =
                nombre.includes(texto) ||
                email.includes(texto);

            const coincideEstado =
                estadoSeleccionado === "TODOS" ||
                estado === estadoSeleccionado;

            if (coincideTexto && coincideEstado) {
                fila.classList.remove("oculto");
                visibles++;
            } else {
                fila.classList.add("oculto");
            }
        });

        if (sinResultados) {
            sinResultados.classList.toggle("oculto", visibles !== 0);
        }
    }

    if (buscador) {
        buscador.addEventListener("input", filtrarAlumnos);
    }

    if (filtroEstado) {
        filtroEstado.addEventListener("change", filtrarAlumnos);
    }

    // ==========================================
    // DROPDOWN DE DATOS EN CADA CARD DE ALUMNO
    // ==========================================
    const botonesToggleAlumno = document.querySelectorAll(".btn-toggle-alumno");

    botonesToggleAlumno.forEach(function (boton) {
        boton.addEventListener("click", function () {
            const card = boton.closest(".alumno-card");

            if (!card) {
                return;
            }

            const estaAbierta = card.classList.toggle("abierta");

            boton.setAttribute("aria-expanded", estaAbierta ? "true" : "false");
        });
    });
});