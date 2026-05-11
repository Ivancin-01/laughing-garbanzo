document.addEventListener("DOMContentLoaded", function () {
    const btnSidebar = document.getElementById("btn-toggle-sidebar");
    const sidebar = document.querySelector(".sidebar");

    const perfilTrigger = document.querySelector(".perfil-trigger");
    const perfilBoton = document.getElementById("perfil-boton");
    const dropdown = document.getElementById("dropdown-perfil");

    const contadores = document.querySelectorAll(".contador");
    const btnImprimirInforme = document.getElementById("btnImprimirInforme");
    const cardsBrillo = document.querySelectorAll(".diagnostico-card, .recomendacion-card");

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

    contadores.forEach(function (contador) {
        const valorFinal = parseInt(contador.dataset.valor || "0", 10);
        let valorActual = 0;
        const duracion = 900;
        const pasos = 30;
        const incremento = valorFinal / pasos;
        const intervalo = duracion / pasos;

        const animacion = setInterval(function () {
            valorActual += incremento;

            if (valorActual >= valorFinal) {
                contador.textContent = valorFinal;
                clearInterval(animacion);
            } else {
                contador.textContent = Math.round(valorActual);
            }
        }, intervalo);
    });

    if (btnImprimirInforme) {
        btnImprimirInforme.addEventListener("click", function () {
            window.print();
        });
    }

    cardsBrillo.forEach(function (card) {
        card.addEventListener("mousemove", function (event) {
            const rect = card.getBoundingClientRect();

            const x = ((event.clientX - rect.left) / rect.width) * 100;
            const y = ((event.clientY - rect.top) / rect.height) * 100;

            card.style.setProperty("--mouse-x", x + "%");
            card.style.setProperty("--mouse-y", y + "%");
        });

        card.addEventListener("mouseleave", function () {
            card.style.setProperty("--mouse-x", "50%");
            card.style.setProperty("--mouse-y", "50%");
        });
    });
});