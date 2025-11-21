document.addEventListener("DOMContentLoaded", () => {

    // ===============================
    //   SELECCIÓN DE SECCIONES
    // ===============================
    document.querySelectorAll(".btn[data-section]").forEach(btn => {
        btn.addEventListener("click", () => {
            const section = btn.getAttribute("data-section");
            showSection(section);
        });
    });

    function showSection(section) {
        const add = document.getElementById("section-add");
        const list = document.getElementById("section-list");

        if (section === "add") {
            add.classList.remove("hidden");
            add.classList.add("active");
            list.classList.add("hidden");
            list.classList.remove("active");
        } else {
            list.classList.remove("hidden");
            list.classList.add("active");
            add.classList.add("hidden");
            add.classList.remove("active");
        }
    }

    showSection("list");

    // ===============================
    //   ELEMENTOS DEL DOM
    // ===============================
    const telefonoInput = document.getElementById("telefono");
    const dniInput = document.getElementById("dni");
    const personasInput = document.getElementById("personas");
    const fechaInput = document.getElementById("fechaReserva");
    const zonaInput = document.getElementById("zona");
    const horaHidden = document.getElementById("horaReserva");
    const horariosContainer = document.getElementById("horariosContainer");
    const montoInput = document.getElementById("monto");
    const form = document.getElementById("formAgregarReserva");

    if (!personasInput || !fechaInput || !zonaInput || !horariosContainer || !montoInput || !form) {
        console.error("Faltan elementos esenciales en el DOM.");
        return;
    }

    // ===============================
    //   VALIDACIONES SIMPLIFICADAS
    // ===============================

    // Teléfono: solo 9 dígitos
    if (telefonoInput) {
        telefonoInput.addEventListener("input", () => {
            telefonoInput.value = telefonoInput.value.replace(/\D/g, "").slice(0, 9);
        });
    }

    // DNI: solo 8 dígitos
    if (dniInput) {
        dniInput.addEventListener("input", () => {
            dniInput.value = dniInput.value.replace(/\D/g, "").slice(0, 8);
        });
    }

    // Personas: entre 1 y 8
    if (personasInput) {
        personasInput.addEventListener("input", () => {
            let n = Number(personasInput.value) || 0;
            if (n < 1) personasInput.value = 1;
            if (n > 8) personasInput.value = 8;
        });
    }

    // Fecha: mínimo hoy (local)
   // Fecha: mínimo hoy o mañana si ya pasaron las 10 PM
// Fecha: mínimo hoy o mañana si ya pasaron las 22:00
if (fechaInput) {
    const ahora = new Date();
    let minFecha = new Date();

    // Si la hora actual es >= 22, la mínima será mañana
    if (ahora.getHours() >= 22) {
        minFecha.setDate(minFecha.getDate() + 1);
    }

    const dia = String(minFecha.getDate()).padStart(2, "0");
    const mes = String(minFecha.getMonth() + 1).padStart(2, "0");
    const anio = minFecha.getFullYear();
    fechaInput.setAttribute("min", `${anio}-${mes}-${dia}`);
}


    // ===============================
    //   CÁLCULO DE MONTO
    // ===============================
    function calcularMonto() {
        const personasNum = Number(personasInput.value) || 0;
        const zonaNombre = zonaInput.options[zonaInput.selectedIndex]?.text || "";
        let precioUnit = 7.00;

        switch (zonaNombre) {
            case "Muelle Panorámico": precioUnit = 8.00; break;
            case "Mirador Azul": precioUnit = 9.00; break;
            case "Salón Bosque": precioUnit = 10.00; break;
        }

        const monto = personasNum * precioUnit;
        montoInput.value = monto ? monto.toFixed(2) : "";
    }

    personasInput.addEventListener("input", calcularMonto);
    zonaInput.addEventListener("change", calcularMonto);

    // ===============================
    //   HORARIOS DISPONIBLES
    // ===============================
    function renderHorarios(lista) {
    horariosContainer.innerHTML = "";

    if (!lista || lista.length === 0) {
        horariosContainer.innerHTML = `<p class="text-danger">No hay horarios disponibles.</p>`;
        return;
    }

    // Obtener la fecha seleccionada
    const fechaSeleccionada = new Date(fechaInput.value);
    const hoy = new Date();
    let listaFiltrada = lista;

    // Filtrar solo horarios futuros si la fecha es hoy
    if (
        fechaSeleccionada.getFullYear() === hoy.getFullYear() &&
        fechaSeleccionada.getMonth() === hoy.getMonth() &&
        fechaSeleccionada.getDate() === hoy.getDate()
    ) {
        const horaActual = hoy.getHours();
        const minutosActual = hoy.getMinutes();

        listaFiltrada = lista.filter(horario => {
            // Suponemos formato "HH:MM"
            const [hh, mm] = horario.split(":").map(Number);
            if (hh > horaActual) return true;
            if (hh === horaActual && mm > minutosActual) return true;
            return false;
        });
    }

    if (listaFiltrada.length === 0) {
        horariosContainer.innerHTML = `<p class="text-danger">No hay horarios disponibles para el resto del día.</p>`;
        return;
    }

    // Renderizar botones
    listaFiltrada.forEach(h => {
        const btn = document.createElement("button");
        btn.textContent = h;
        btn.type = "button";
        btn.className = "hora-btn";
        btn.addEventListener("click", () => {
            horaHidden.value = h;
            horariosContainer.querySelectorAll("button").forEach(b => b.classList.remove("active"));
            btn.classList.add("active");
        });
        horariosContainer.appendChild(btn);
    });
}



    async function cargarHorarios() {
        if (!personasInput.value || !fechaInput.value || !zonaInput.value) {
            horariosContainer.innerHTML = `<p class="text-muted">Selecciona primero personas, zona y fecha.</p>`;
            return;
        }

        horariosContainer.innerHTML = `<p>Cargando...</p>`;

        const url = `/api/horarios/disponibles?personas=${personasInput.value}&fechaReserva=${fechaInput.value}&zona=${zonaInput.value}`;
        try {
            const resp = await fetch(url);
            const data = await resp.json();
            renderHorarios(data);
        } catch (error) {
            console.error("Error cargando horarios:", error);
            horariosContainer.innerHTML = `<p class="text-danger">Error cargando horarios.</p>`;
        }
    }

    personasInput.addEventListener("change", cargarHorarios);
    fechaInput.addEventListener("change", cargarHorarios);
    zonaInput.addEventListener("change", cargarHorarios);

    // ===============================
    //   ENVÍO DEL FORMULARIO
    // ===============================
    form.addEventListener("submit", (e) => {
        if (!horaHidden.value) {
            e.preventDefault(); // evitar envío si no se seleccionó hora
            alert("Debes seleccionar un horario.");
        }
    });

});
