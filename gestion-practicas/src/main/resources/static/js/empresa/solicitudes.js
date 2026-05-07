document.addEventListener("DOMContentLoaded", function () {
    const buscador = document.getElementById("buscadorSolicitudes");
    const filtroEstado = document.getElementById("filtroEstado");
    const filas = document.querySelectorAll(".fila-solicitud");
    const sinResultados = document.getElementById("sinResultados");
    const botonesConfirmar = document.querySelectorAll(".btn-confirmar");

    function filtrarSolicitudes() {
        const textoBusqueda = buscador ? buscador.value.toLowerCase().trim() : "";
        const estadoSeleccionado = filtroEstado ? filtroEstado.value : "TODAS";

        let visibles = 0;

        filas.forEach(function (fila) {
            const alumno = (fila.dataset.alumno || "").toLowerCase();
            const oferta = (fila.dataset.oferta || "").toLowerCase();
            const estado = fila.dataset.estado || "";

            const coincideTexto =
                alumno.includes(textoBusqueda) ||
                oferta.includes(textoBusqueda);

            const coincideEstado =
                estadoSeleccionado === "TODAS" ||
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
        buscador.addEventListener("input", filtrarSolicitudes);
    }

    if (filtroEstado) {
        filtroEstado.addEventListener("change", filtrarSolicitudes);
    }

    botonesConfirmar.forEach(function (boton) {
        boton.addEventListener("click", function (event) {
            const mensaje = boton.dataset.mensaje || "¿Confirmas esta acción?";

            if (!confirm(mensaje)) {
                event.preventDefault();
            }
        });
    });
});