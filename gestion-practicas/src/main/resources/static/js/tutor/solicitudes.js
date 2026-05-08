document.addEventListener("DOMContentLoaded", function () {
    console.log("✅ solicitudes.js tutor cargado");

    inicializarNavbar();
    inicializarFiltrosSolicitudes();
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

function inicializarFiltrosSolicitudes() {
    const inputBusqueda = document.getElementById("buscarSolicitud");
    const filtroEstado = document.getElementById("filtroEstado");
    const btnLimpiar = document.getElementById("btn-limpiar-filtros");

    const filas = Array.from(document.querySelectorAll(".fila-solicitud"));
    const contador = document.getElementById("contador-solicitudes");
    const sinResultados = document.getElementById("sin-resultados");

    if (!inputBusqueda || !filtroEstado || filas.length === 0) {
        return;
    }

    const aplicarFiltros = function () {
        const texto = normalizarTexto(inputBusqueda.value);
        const estado = normalizarTexto(filtroEstado.value);

        let visibles = 0;

        filas.forEach(function (fila) {
            const alumno = normalizarTexto(fila.dataset.alumno || "");
            const correo = normalizarTexto(fila.dataset.correo || "");
            const oferta = normalizarTexto(fila.dataset.oferta || "");
            const empresa = normalizarTexto(fila.dataset.empresa || "");
            const estadoSolicitud = normalizarTexto(fila.dataset.estado || "");

            const coincideTexto =
                !texto ||
                alumno.includes(texto) ||
                correo.includes(texto) ||
                oferta.includes(texto) ||
                empresa.includes(texto);

            const coincideEstado = !estado || estadoSolicitud.includes(estado);

            const visible = coincideTexto && coincideEstado;

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
    filtroEstado.addEventListener("change", aplicarFiltros);

    if (btnLimpiar) {
        btnLimpiar.addEventListener("click", function () {
            inputBusqueda.value = "";
            filtroEstado.value = "";
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