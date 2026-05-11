document.addEventListener("DOMContentLoaded", function () {
    const btnSidebar = document.getElementById("btn-toggle-sidebar");
    const sidebar = document.querySelector(".sidebar");

    const perfilTrigger = document.querySelector(".perfil-trigger");
    const perfilBoton = document.getElementById("perfil-boton");
    const dropdown = document.getElementById("dropdown-perfil");

    const cardsBrillo = document.querySelectorAll(".resumen-card, .formulario-card");

    const formPerfil = document.querySelector(".form-perfil");
    const inputNombre = document.getElementById("nombre");
    const inputApellidos = document.getElementById("apellidos");
    const inputTelefono = document.getElementById("telefono");

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

    function limpiarError(input) {
        const campo = input.closest(".campo");

        if (!campo) {
            return;
        }

        campo.classList.remove("campo-error");

        const errorAnterior = campo.querySelector(".mensaje-error");

        if (errorAnterior) {
            errorAnterior.remove();
        }
    }

    function mostrarError(input, mensaje) {
        const campo = input.closest(".campo");

        if (!campo) {
            return;
        }

        limpiarError(input);

        campo.classList.add("campo-error");

        const error = document.createElement("small");
        error.className = "mensaje-error";
        error.innerHTML = `<i class="bi bi-exclamation-circle"></i> ${mensaje}`;

        campo.appendChild(error);
    }

    function validarTextoObligatorio(input, mensaje) {
        if (!input) {
            return true;
        }

        const valor = input.value.trim();

        if (valor.length < 2) {
            mostrarError(input, mensaje);
            return false;
        }

        limpiarError(input);
        return true;
    }

    function validarTelefono(input) {
        if (!input) {
            return true;
        }

        const valor = input.value.trim();

        if (valor === "") {
            limpiarError(input);
            return true;
        }

        const telefonoValido = /^[0-9+\s()-]{7,20}$/.test(valor);

        if (!telefonoValido) {
            mostrarError(input, "Introduce un teléfono válido.");
            return false;
        }

        limpiarError(input);
        return true;
    }

    if (inputNombre) {
        inputNombre.addEventListener("input", function () {
            validarTextoObligatorio(inputNombre, "El nombre debe tener al menos 2 caracteres.");
        });
    }

    if (inputApellidos) {
        inputApellidos.addEventListener("input", function () {
            validarTextoObligatorio(inputApellidos, "Los apellidos deben tener al menos 2 caracteres.");
        });
    }

    if (inputTelefono) {
        inputTelefono.addEventListener("input", function () {
            validarTelefono(inputTelefono);
        });
    }

    if (formPerfil) {
        formPerfil.addEventListener("submit", function (event) {
            const nombreOk = validarTextoObligatorio(
                inputNombre,
                "El nombre debe tener al menos 2 caracteres."
            );

            const apellidosOk = validarTextoObligatorio(
                inputApellidos,
                "Los apellidos deben tener al menos 2 caracteres."
            );

            const telefonoOk = validarTelefono(inputTelefono);

            if (!nombreOk || !apellidosOk || !telefonoOk) {
                event.preventDefault();

                const primerError = document.querySelector(".campo-error input");

                if (primerError) {
                    primerError.focus();
                }
            }
        });
    }
});