document.addEventListener('DOMContentLoaded', function() {
    console.log("Configuración: Sistema inicializado de forma independiente.");

    /* ==========================================================================
       1. LÓGICA DE LA BARRA DE NAVEGACIÓN (DROPDOWN)
       ========================================================================== */
    
    const usuarioContainer = document.querySelector('.usuario-nav-container');
    const dropdown = document.getElementById("dropdown-perfil");

    // Función para abrir/cerrar el menú
    // Definimos window.toggleDropdown para que el 'onclick' del HTML siga funcionando
    window.toggleDropdown = function() {
        if (dropdown) {
            dropdown.classList.toggle("show");
        }
    };

    // Cerrar el dropdown al hacer clic en cualquier parte fuera del menú
    window.addEventListener('click', function(event) {
        if (!event.target.closest('.usuario-nav-container')) {
            if (dropdown && dropdown.classList.contains('show')) {
                dropdown.classList.remove('show');
            }
        }
    });


    /* ==========================================================================
       2. LÓGICA DE LOS AJUSTES (SEGURIDAD Y PRIVACIDAD)
       ========================================================================== */

    // Actualizar Contraseña
    const btnActualizar = document.querySelector('.btn-guardar-mini');
    if (btnActualizar) {
        btnActualizar.addEventListener('click', function() {
            const passInput = document.querySelector('input[type="password"]');
            if (passInput && passInput.value.trim() !== "") {
                alert("Solicitud de cambio de contraseña enviada.");
                passInput.value = ""; 
            } else {
                alert("Por favor, introduce una nueva contraseña.");
            }
        });
    }

    // Switch de Privacidad
    const privacyCheck = document.querySelector('.switch input');
    if (privacyCheck) {
        privacyCheck.addEventListener('change', function() {
            const mensaje = this.checked ? "Perfil ahora es PÚBLICO" : "Perfil ahora es PRIVADO";
            console.log(mensaje);
        });
    }

    // Zona Crítica - Borrar Cuenta
    const btnBorrar = document.querySelector('.btn-danger');
    if (btnBorrar) {
        btnBorrar.addEventListener('click', function() {
            const confirmar = confirm("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción es irreversible.");
            if (confirmar) {
                const palabra = prompt("Escribe 'ELIMINAR' para confirmar:");
                if (palabra === "ELIMINAR") {
                    alert("Cuenta eliminada correctamente.");
                    // window.location.href = "/logout";
                }
            }
        });
    }
});