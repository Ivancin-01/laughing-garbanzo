document.addEventListener("DOMContentLoaded", function () {
    console.log("✅ tutores.js tutor-centro cargado");

    inicializarNavbar();
    inicializarFiltrosTutores();
});

function inicializarNavbar() {
    const btnSidebar = document.getElementById("btn-toggle-sidebar");
    const sidebar = document.querySelector(".sidebar");

    const perfilTrigger = document.querySelector(".perfil-trigger");
    const perfilBoton = document.getElementById("perfil-boton");
    const dropdown = document.getElementById("dropdown-perfil");

    if (btnSidebar && sidebar) {
        btnSidebar.addEventListener("click", function () {
            sidebar.classList.toggle("oculto");
        });
    }

    if (perfilBoton && dropdown && perfilTrigger) {
        perfilBoton.addEventListener("click", function (event) {
            event.preventDefault();
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
}

function inicializarFiltrosTutores() {
    const inputBusqueda = document.getElementById("buscarTutor");
    const filtroDisponibilidad = document.getElementById("filtroDisponibilidad");
    const btnLimpiar = document.getElementById("btn-limpiar-filtros");

    const filas = Array.from(document.querySelectorAll(".fila-tutor"));
    const contador = document.getElementById("contador-tutores");
    const sinResultados = document.getElementById("sin-resultados");

    if (!inputBusqueda || !filtroDisponibilidad || filas.length === 0) {
        return;
    }

    const aplicarFiltros = function () {
        const texto = normalizarTexto(inputBusqueda.value);
        const disponibilidad = normalizarTexto(filtroDisponibilidad.value);

        let visibles = 0;

        filas.forEach(function (fila) {
            const nombre = normalizarTexto(fila.dataset.nombre || "");
            const correo = normalizarTexto(fila.dataset.correo || "");
            const especialidad = normalizarTexto(fila.dataset.especialidad || "");
            const departamento = normalizarTexto(fila.dataset.departamento || "");
            const disponibilidadTutor = normalizarTexto(fila.dataset.disponibilidad || "");

            const coincideTexto =
                !texto ||
                nombre.includes(texto) ||
                correo.includes(texto) ||
                especialidad.includes(texto) ||
                departamento.includes(texto);

            const coincideDisponibilidad =
                !disponibilidad ||
                disponibilidadTutor === disponibilidad;

            const visible = coincideTexto && coincideDisponibilidad;

            fila.classList.toggle("oculto", !visible);

            if (visible) {
                visibles++;
            }
        });

        if (contador) {
            contador.textContent = visibles;
        }

        if (sinResultados) {
            sinResultados.classList.toggle("oculto", visibles !== 0);
        }
    };

    inputBusqueda.addEventListener("input", aplicarFiltros);
    filtroDisponibilidad.addEventListener("change", aplicarFiltros);

    if (btnLimpiar) {
        btnLimpiar.addEventListener("click", function () {
            inputBusqueda.value = "";
            filtroDisponibilidad.value = "";
            aplicarFiltros();
        });
    }
}

function normalizarTexto(texto) {
    return texto
        .toString()
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .trim();
}