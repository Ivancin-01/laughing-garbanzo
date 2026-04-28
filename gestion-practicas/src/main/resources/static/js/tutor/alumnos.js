document.addEventListener('DOMContentLoaded', function() {

    // Toggle dropdown perfil
    window.toggleDropdown = function() {
        const dropdown = document.getElementById('dropdown-perfil');
        if (dropdown) dropdown.classList.toggle('show');
    };

    window.addEventListener('click', function(event) {
        if (!event.target.closest('.perfil-trigger')) {
            const dropdown = document.getElementById('dropdown-perfil');
            if (dropdown && dropdown.classList.contains('show')) {
                dropdown.classList.remove('show');
            }
        }
    });

    // Filtros de la tabla de alumnos
    const inputBuscar    = document.getElementById('buscarAlumno');
    const selectEstado   = document.getElementById('filtroEstado');
    const selectEsp      = document.getElementById('filtroEspecialidad');

    function filtrar() {
        const texto = inputBuscar ? inputBuscar.value.toLowerCase() : '';
        const estado = selectEstado ? selectEstado.value.toLowerCase() : '';
        const esp    = selectEsp ? selectEsp.value.toLowerCase() : '';

        document.querySelectorAll('.fila-alumno').forEach(function(fila) {
            const nombre = fila.querySelector('td strong') 
                ? fila.querySelector('td strong').textContent.toLowerCase() : '';
            const filaEstado = (fila.getAttribute('data-estado') || '').toLowerCase();
            const filaEsp    = (fila.getAttribute('data-especialidad') || '').toLowerCase();

            const pasaTexto  = !texto  || nombre.includes(texto);
            const pasaEstado = !estado || filaEstado.includes(estado);
            const pasaEsp    = !esp    || filaEsp.includes(esp);

            fila.style.display = (pasaTexto && pasaEstado && pasaEsp) ? '' : 'none';
        });
    }

    if (inputBuscar)  inputBuscar.addEventListener('input', filtrar);
    if (selectEstado) selectEstado.addEventListener('change', filtrar);
    if (selectEsp)    selectEsp.addEventListener('change', filtrar);
});