document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("formRecuperar");
  const email = document.getElementById("email");
  const errorEmail = document.getElementById("err-email");

  if (!form || !email || !errorEmail) return;

  form.addEventListener("submit", (e) => {
    errorEmail.textContent = "";

    const valorEmail = email.value.trim();

    if (valorEmail === "") {
      e.preventDefault();
      errorEmail.textContent = "El correo electrónico es obligatorio.";
      email.focus();
      return;
    }

    const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!regexEmail.test(valorEmail)) {
      e.preventDefault();
      errorEmail.textContent = "Introduce un correo electrónico válido.";
      email.focus();
    }
  });
});