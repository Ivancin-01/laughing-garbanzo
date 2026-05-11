document.addEventListener("DOMContentLoaded", function () {
    const buscador = document.getElementById("buscadorOfertas");
    const filtroSolicitudes = document.getElementById("filtroSolicitudes");
    const filas = document.querySelectorAll(".fila-oferta");
    const sinResultados = document.getElementById("sinResultadosOfertas");
    const botonesEliminar = document.querySelectorAll(".btn-eliminar-oferta");

    function filtrarOfertas() {
        const textoBusqueda = buscador ? buscador.value.toLowerCase().trim() : "";
        const filtro = filtroSolicitudes ? filtroSolicitudes.value : "TODAS";

        let visibles = 0;

        filas.forEach(function (fila) {
            const titulo = (fila.dataset.titulo || "").toLowerCase();
            const modalidad = (fila.dataset.modalidad || "").toLowerCase();
            const especialidad = (fila.dataset.especialidad || "").toLowerCase();
            const solicitudes = Number(fila.dataset.solicitudes || 0);

            const coincideTexto =
                titulo.includes(textoBusqueda) ||
                modalidad.includes(textoBusqueda) ||
                especialidad.includes(textoBusqueda);

            const coincideFiltro =
                filtro === "TODAS" ||
                (filtro === "CON_SOLICITUDES" && solicitudes > 0) ||
                (filtro === "SIN_SOLICITUDES" && solicitudes === 0);

            if (coincideTexto && coincideFiltro) {
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
        buscador.addEventListener("input", filtrarOfertas);
    }

    if (filtroSolicitudes) {
        filtroSolicitudes.addEventListener("change", filtrarOfertas);
    }

    botonesEliminar.forEach(function (boton) {
        boton.addEventListener("click", function (event) {
            const mensaje = boton.dataset.mensaje || "¿Seguro que quieres eliminar esta oferta?";

            if (!confirm(mensaje)) {
                event.preventDefault();
            }
        });
    });
});