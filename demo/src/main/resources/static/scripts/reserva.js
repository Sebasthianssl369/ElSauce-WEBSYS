document.addEventListener("DOMContentLoaded", () => {
  const steps = Array.from(document.querySelectorAll(".reserva-step"));
  const timelineSteps = Array.from(document.querySelectorAll(".timeline .step"));
  const btnSiguientes = Array.from(document.querySelectorAll(".btn-siguiente"));
  const btnAnteriores = Array.from(document.querySelectorAll(".btn-anterior"));
  const resumenDiv = document.getElementById("resumen-reserva");
  const resumenFinal = document.getElementById("resumen-final");

  const reservaData = { personas: 2, fecha: null, hora: null, zona: null, monto: 0 };

  function getActiveStepIndex() {
    return steps.findIndex(s => s.classList.contains("active"));
  }

  function showStep(index) {
    steps.forEach((s, i) => s.classList.toggle("active", i === index));
    timelineSteps.forEach((t, i) => t.classList.toggle("active", i <= index));
    actualizarResumen();
  }

  btnSiguientes.forEach(btn => btn.addEventListener("click", () => {
    const current = getActiveStepIndex();

    if (current === 0) {
      const personas = parseInt(document.getElementById("personas").value);
      if (personas < 2) {
        alert("❌ El mínimo es 2 personas.");
        return;
      }
      reservaData.personas = personas;
    }

    if (current === 1 && !reservaData.fecha) {
      alert("📅 Selecciona una fecha.");
      return;
    }

    if (current === 2 && !reservaData.hora) {
      alert("🕒 Selecciona una hora.");
      return;
    }

    if (current === 3 && !reservaData.zona) {
      alert("📍 Selecciona una zona.");
      return;
    }

    // ✅ Cuando está en paso 5 y avanza al 6, guardamos los datos personales
    if (current === 4) {
      const nombre = document.getElementById("nombre").value.trim();
      const apellidos = document.getElementById("apellidos").value.trim();
      const email = document.getElementById("email").value.trim();
      const telefono = document.getElementById("telefono").value.trim();
      const dni = document.getElementById("dni").value.trim();

      if (!nombre || !apellidos || !email || !telefono || !dni) {
        alert("⚠️ Por favor, completa todos los datos personales antes de continuar.");
        return;
      }

      reservaData.nombre = nombre;
      reservaData.apellidos = apellidos;
      reservaData.email = email;
      reservaData.telefono = telefono;
      reservaData.dni = dni;
    }

    if (current < steps.length - 1) showStep(current + 1);
  }));

  btnAnteriores.forEach(btn => btn.addEventListener("click", () => {
    const current = getActiveStepIndex();
    if (current > 0) showStep(current - 1);
  }));

  // --- Calendario y horas omitidos (mantén los tuyos iguales) ---

  // ----------------------------
  // Calcular y mostrar resumen
  // ----------------------------
  function actualizarResumen() {
    let monto = 0;
    if (reservaData.zona && reservaData.personas) {
      switch (reservaData.zona.nombre) {
        case "Muelle Panorámico": monto = reservaData.personas * 8.00; break;
        case "Mirador Azul": monto = reservaData.personas * 9.00; break;
        case "Salón Bosque": monto = reservaData.personas * 10.00; break;
        default: monto = reservaData.personas * 7.00; break;
      }
    }
    reservaData.monto = monto;

    if (resumenDiv) {
      resumenDiv.innerHTML = `
        <p><strong>Personas:</strong> ${reservaData.personas || ""}</p>
        <p><strong>Fecha:</strong> ${reservaData.fecha ? reservaData.fecha.toLocaleDateString() : ""}</p>
        <p><strong>Hora:</strong> ${reservaData.hora || ""}</p>
        <p><strong>Zona:</strong> ${reservaData.zona?.nombre || ""}</p>
        <p><strong>Monto a pagar:</strong> S/ ${monto.toFixed(2)}</p>
      `;
    }

    if (resumenFinal) {
      resumenFinal.innerHTML = `
        <h3>Resumen de la reserva</h3>
        <p><strong>Nombre:</strong> ${reservaData.nombre || ""} ${reservaData.apellidos || ""}</p>
        <p><strong>Email:</strong> ${reservaData.email || ""}</p>
        <p><strong>Teléfono:</strong> ${reservaData.telefono || ""}</p>
        <p><strong>Personas:</strong> ${reservaData.personas || ""}</p>
        <p><strong>Fecha:</strong> ${reservaData.fecha ? reservaData.fecha.toLocaleDateString() : ""}</p>
        <p><strong>Hora:</strong> ${reservaData.hora || ""}</p>
        <p><strong>Zona:</strong> ${reservaData.zona?.nombre || ""}</p>
        <p><strong>Monto a pagar:</strong> S/ ${monto.toFixed(2)}</p>
      `;
    }
  }

  // ----------------------------
  // Confirmar y pagar
  // ----------------------------
  const btnConfirmar = document.getElementById("btn-confirmar");
  if (btnConfirmar) {
    btnConfirmar.addEventListener("click", (e) => {
      e.preventDefault();

      const nombreTarjeta = document.getElementById("nombre-tarjeta");
      const numeroTarjeta = document.getElementById("numero-tarjeta");
      const cvv = document.getElementById("cvv");

      if (!nombreTarjeta.value || !numeroTarjeta.value || !cvv.value) {
        alert("Por favor, completa todos los datos de la tarjeta.");
        return;
      }

      if (numeroTarjeta.value.length < 12 || numeroTarjeta.value.length > 19) {
        alert("El número de tarjeta debe tener entre 12 y 19 dígitos.");
        return;
      }

      if (cvv.value.length < 3 || cvv.value.length > 4) {
        alert("El CVV debe tener 3 o 4 dígitos.");
        return;
      }

      alert(`✅ ¡Reserva confirmada!\nMonto pagado: S/ ${reservaData.monto.toFixed(2)}\nGracias por tu preferencia.`);
      window.location.href = "/";
    });
  }
});
