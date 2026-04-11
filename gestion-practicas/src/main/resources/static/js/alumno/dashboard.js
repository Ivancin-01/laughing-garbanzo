// Esperamos a que todo el DOM esté cargado para evitar errores de "not defined"
document.addEventListener('DOMContentLoaded', function() {
    // Configuración del Sidebar
    const btnToggle = document.getElementById('toggle-sidebar');
    if (btnToggle) {
        btnToggle.addEventListener('click', function() {
            document.querySelector('.sidebar').classList.toggle('oculto');
        });
    }

    // Definimos la función globalmente para que el onclick del HTML la vea
    window.toggleDropdown = function() {
        const dropdown = document.getElementById("dropdown-perfil");
        if (dropdown) {
            dropdown.classList.toggle("show");
        }
    };

    // Cerrar el dropdown si se hace clic fuera
    window.addEventListener('click', function(event) {
        if (!event.target.closest('.perfil-trigger')) {
            const dropdown = document.getElementById("dropdown-perfil");
            if (dropdown && dropdown.classList.contains('show')) {
                dropdown.classList.remove('show');
            }
        }
    });
});



