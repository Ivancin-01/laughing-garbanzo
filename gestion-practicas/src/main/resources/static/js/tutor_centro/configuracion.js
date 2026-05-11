document.addEventListener("DOMContentLoaded", function () {
    const btnSidebar = document.getElementById("btn-toggle-sidebar");
    const sidebar = document.querySelector(".sidebar");

    const perfilTrigger = document.querySelector(".perfil-trigger");
    const perfilBoton = document.getElementById("perfil-boton");
    const dropdown = document.getElementById("dropdown-perfil");

    const botonesVerPassword = document.querySelectorAll(".btn-ver-password");

    const formPassword = document.getElementById("formPassword");
    const passwordActual = document.getElementById("passwordActual");
    const nuevaPassword = document.getElementById("nuevaPassword");
    const confirmarPassword = document.getElementById("confirmarPassword");
    const passwordStrengthBar = document.getElementById("passwordStrengthBar");
    const passwordHelp = document.getElementById("passwordHelp");

    const inputConfirmacion = document.getElementById("confirmacion");
    const btnDesactivar = document.getElementById("btnDesactivar");
    const formDesactivar = document.getElementById("formDesactivar");

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

    botonesVerPassword.forEach(function (boton) {
        boton.addEventListener("click", function () {
            const targetId = boton.dataset.target;
            const input = document.getElementById(targetId);
            const icono = boton.querySelector("i");

            if (!input) {
                return;
            }

            if (input.type === "password") {
                input.type = "text";

                if (icono) {
                    icono.classList.remove("bi-eye");
                    icono.classList.add("bi-eye-slash");
                }
            } else {
                input.type = "password";

                if (icono) {
                    icono.classList.remove("bi-eye-slash");
                    icono.classList.add("bi-eye");
                }
            }
        });
    });

    function limpiarError(input) {
        if (!input) {
            return;
        }

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
        if (!input) {
            return;
        }

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

    function calcularFuerzaPassword(password) {
        let puntos = 0;

        if (password.length >= 6) {
            puntos++;
        }

        if (/[A-ZÁÉÍÓÚÑ]/.test(password) && /[a-záéíóúñ]/.test(password)) {
            puntos++;
        }

        if (/[0-9]/.test(password)) {
            puntos++;
        }

        if (/[^A-Za-z0-9ÁÉÍÓÚÑáéíóúñ]/.test(password)) {
            puntos++;
        }

        return puntos;
    }

    function actualizarBarraPassword() {
        if (!nuevaPassword || !passwordStrengthBar || !passwordHelp) {
            return;
        }

        const valor = nuevaPassword.value.trim();
        const fuerza = calcularFuerzaPassword(valor);

        passwordStrengthBar.classList.remove("debil", "media", "fuerte");

        if (valor.length === 0) {
            passwordStrengthBar.style.width = "0";
            passwordHelp.textContent = "Usa al menos 6 caracteres. Mejor si combinas letras, números y símbolos.";
            return;
        }

        passwordStrengthBar.style.width = "";

        if (fuerza <= 1) {
            passwordStrengthBar.classList.add("debil");
            passwordHelp.textContent = "Contraseña débil.";
        } else if (fuerza <= 3) {
            passwordStrengthBar.classList.add("media");
            passwordHelp.textContent = "Contraseña aceptable.";
        } else {
            passwordStrengthBar.classList.add("fuerte");
            passwordHelp.textContent = "Contraseña fuerte.";
        }
    }

    function validarPasswordForm() {
        let valido = true;

        if (!passwordActual || passwordActual.value.trim().length === 0) {
            mostrarError(passwordActual, "Introduce tu contraseña actual.");
            valido = false;
        } else {
            limpiarError(passwordActual);
        }

        if (!nuevaPassword || nuevaPassword.value.trim().length < 6) {
            mostrarError(nuevaPassword, "La nueva contraseña debe tener al menos 6 caracteres.");
            valido = false;
        } else {
            limpiarError(nuevaPassword);
        }

        if (!confirmarPassword || confirmarPassword.value.trim() !== nuevaPassword.value.trim()) {
            mostrarError(confirmarPassword, "Las contraseñas nuevas no coinciden.");
            valido = false;
        } else {
            limpiarError(confirmarPassword);
        }

        return valido;
    }

    if (nuevaPassword) {
        nuevaPassword.addEventListener("input", actualizarBarraPassword);
    }

    if (confirmarPassword) {
        confirmarPassword.addEventListener("input", function () {
            if (confirmarPassword.value.trim() === nuevaPassword.value.trim()) {
                limpiarError(confirmarPassword);
            }
        });
    }

    if (formPassword) {
        formPassword.addEventListener("submit", function (event) {
            if (!validarPasswordForm()) {
                event.preventDefault();

                const primerError = document.querySelector(".campo-error input");

                if (primerError) {
                    primerError.focus();
                }
            }
        });
    }

    if (inputConfirmacion && btnDesactivar) {
        inputConfirmacion.addEventListener("input", function () {
            btnDesactivar.disabled = inputConfirmacion.value.trim() !== "DESACTIVAR";
        });
    }

    if (formDesactivar) {
        formDesactivar.addEventListener("submit", function (event) {
            const confirmado = inputConfirmacion && inputConfirmacion.value.trim() === "DESACTIVAR";

            if (!confirmado) {
                event.preventDefault();
                alert("Debes escribir DESACTIVAR para confirmar la acción.");
                return;
            }

            const seguro = confirm("¿Seguro que quieres desactivar tu cuenta? Se cerrará tu sesión.");

            if (!seguro) {
                event.preventDefault();
            }
        });
    }
});