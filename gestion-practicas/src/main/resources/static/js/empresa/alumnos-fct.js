document.addEventListener("DOMContentLoaded", function () {
    const buscador = document.getElementById("buscadorAlumnos");
    const tarjetas = document.querySelectorAll(".alumno-card");

    const fichaVacia = document.getElementById("fichaVacia");
    const fichaContenido = document.getElementById("fichaContenido");

    const fichaInicial = document.getElementById("fichaInicial");
    const fichaNombre = document.getElementById("fichaNombre");
    const fichaEmail = document.getElementById("fichaEmail");
    const fichaOferta = document.getElementById("fichaOferta");
    const fichaCiclo = document.getElementById("fichaCiclo");
    const fichaCentro = document.getElementById("fichaCentro");

    const fichaEmailLink = document.getElementById("fichaEmailLink");
    const fichaCv = document.getElementById("fichaCv");
    const fichaSinCv = document.getElementById("fichaSinCv");
    const estadoSolicitudId = document.getElementById("estadoSolicitudId");
    const estadoPracticaSelect = document.getElementById("estadoPractica");

    function mostrarFicha(tarjeta) {
        const id = tarjeta.dataset.id || "";
        const nombre = tarjeta.dataset.nombre || "Alumno";
        const email = tarjeta.dataset.email || "Sin email";
        const ciclo = tarjeta.dataset.ciclo || "No indicado";
        const centro = tarjeta.dataset.centro || "No indicado";
        const oferta = tarjeta.dataset.oferta || "Sin oferta";
        const estadoPractica = tarjeta.dataset.estadoPractica || "PENDIENTE_INICIO";
        const cv = tarjeta.dataset.cv || "";

        tarjetas.forEach(function (item) {
            item.classList.remove("activo");
        });

        tarjeta.classList.add("activo");

        fichaInicial.textContent = nombre.charAt(0).toUpperCase();
        fichaNombre.textContent = nombre;
        fichaEmail.textContent = email;
        fichaOferta.textContent = oferta;
        fichaCiclo.textContent = ciclo;
        fichaCentro.textContent = centro;

        if (estadoSolicitudId) {
            estadoSolicitudId.value = id;
        }

        if (estadoPracticaSelect) {
            estadoPracticaSelect.value = estadoPractica;
        }

        fichaEmailLink.href = "mailto:" + email;

        fichaVacia.classList.add("oculto");
        fichaContenido.classList.remove("oculto");

        if (cv.trim() !== "") {
            fichaCv.href = cv;
            fichaCv.classList.remove("oculto");
            fichaSinCv.classList.add("oculto");
        } else {
            fichaCv.classList.add("oculto");
            fichaSinCv.classList.remove("oculto");
        }
    }

    function filtrarAlumnos() {
        const texto = buscador ? buscador.value.toLowerCase().trim() : "";

        tarjetas.forEach(function (tarjeta) {
            const nombre = (tarjeta.dataset.nombre || "").toLowerCase();
            const oferta = (tarjeta.dataset.oferta || "").toLowerCase();
            const ciclo = (tarjeta.dataset.ciclo || "").toLowerCase();

            const coincide =
                nombre.includes(texto) ||
                oferta.includes(texto) ||
                ciclo.includes(texto);

            tarjeta.classList.toggle("oculto", !coincide);
        });
    }

    tarjetas.forEach(function (tarjeta) {
        tarjeta.addEventListener("click", function () {
            mostrarFicha(tarjeta);
        });
    });

    if (buscador) {
        buscador.addEventListener("input", filtrarAlumnos);
    }

    if (tarjetas.length === 1) {
        mostrarFicha(tarjetas[0]);
    }
});