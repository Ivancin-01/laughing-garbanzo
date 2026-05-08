document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("formCrearOferta");
    const titulo = document.getElementById("titulo");
    const descripcion = document.getElementById("descripcion");

    if (!form) {
        return;
    }

    form.addEventListener("submit", function (event) {
        const tituloValor = titulo ? titulo.value.trim() : "";
        const descripcionValor = descripcion ? descripcion.value.trim() : "";

        if (tituloValor.length < 5) {
            event.preventDefault();
            alert("Por favor, introduce un título más descriptivo.");
            titulo.focus();
            return;
        }

        if (descripcionValor.length < 20) {
            event.preventDefault();
            alert("La descripción debe tener al menos 20 caracteres.");
            descripcion.focus();
        }
    });
});