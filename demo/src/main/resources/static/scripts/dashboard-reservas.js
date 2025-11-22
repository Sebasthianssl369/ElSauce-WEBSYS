document.addEventListener("DOMContentLoaded", () => {

    // ===============================
    // SELECCIÓN DE SECCIONES
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
    // ELEMENTOS DEL DOM
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

    // ===============================
    // BLOQUEAR DÍAS PASADOS
    // ===============================
    if (fechaInput) {
        const hoy = new Date();
        const yyyy = hoy.getFullYear();
        const mm = String(hoy.getMonth() + 1).padStart(2, "0");
        const dd = String(hoy.getDate()).padStart(2, "0");
        fechaInput.setAttribute("min", `${yyyy}-${mm}-${dd}`);
    }

    // ===============================
    // VALIDACIONES SIMPLIFICADAS
    // ===============================
    if (telefonoInput) {
        telefonoInput.addEventListener("input", () => {
            telefonoInput.value = telefonoInput.value.replace(/\D/g, "").slice(0, 9);
        });
    }

    if (dniInput) {
        dniInput.addEventListener("input", () => {
            dniInput.value = dniInput.value.replace(/\D/g, "").slice(0, 8);
        });
    }

    personasInput.addEventListener("input", () => {
        let n = Number(personasInput.value) || 0;
        if (n < 1) personasInput.value = 1;
        if (n > 8) personasInput.value = 8;
    });

    // ===============================
    // CÁLCULO DEL MONTO
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
    // PARSE FECHA LOCAL
    // ===============================
    function parseFechaLocal(yyyy_mm_dd) {
        const [yyyy, mm, dd] = yyyy_mm_dd.split("-").map(Number);
        return new Date(yyyy, mm - 1, dd);
    }

    // ===============================
    // RENDER DE HORARIOS
    // ===============================
    function renderHorarios(lista) {
        horariosContainer.innerHTML = "";

        if (!lista || lista.length === 0) {
            horariosContainer.innerHTML = `<p class="text-danger">No hay horarios disponibles.</p>`;
            return;
        }

        const hoy = new Date();
        const fechaSel = parseFechaLocal(fechaInput.value);

        let esHoy =
            fechaSel.getFullYear() === hoy.getFullYear() &&
            fechaSel.getMonth() === hoy.getMonth() &&
            fechaSel.getDate() === hoy.getDate();

        let listaFiltrada = lista;

        if (esHoy) {
            const horaActual = hoy.getHours();
            const minutoActual = hoy.getMinutes();

            listaFiltrada = lista.filter(h => {
                const [hh, mm] = h.split(":").map(Number);

                if (hh > horaActual) return true;
                if (hh === horaActual && mm > minutoActual) return true;
                return false;
            });
        }

        if (listaFiltrada.length === 0) {
            horariosContainer.innerHTML = `<p class="text-danger">No hay horarios disponibles para el resto del día.</p>`;
            return;
        }

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

    // ===============================
    // CARGAR HORARIOS DESDE API
    // ===============================
    async function cargarHorarios() {
        if (!personasInput.value || !fechaInput.value || !zonaInput.value) {
            horariosContainer.innerHTML = `<p class="text-muted">Selecciona primero personas, zona y fecha.</p>`;
            return;
        }

        horariosContainer.innerHTML = `<p>Cargando...</p>`;

        const url =
            `/api/horarios/disponibles?personas=${personasInput.value}` +
            `&fechaReserva=${fechaInput.value}&zona=${zonaInput.value}`;

        try {
            const resp = await fetch(url);
            const data = await resp.json();

            const listaNormalizada = data.map(h => h.substring(0, 5));

            renderHorarios(listaNormalizada);
        } catch (error) {
            console.error("Error cargando horarios:", error);
            horariosContainer.innerHTML = `<p class="text-danger">Error cargando horarios.</p>`;
        }
    }

    personasInput.addEventListener("change", cargarHorarios);
    fechaInput.addEventListener("change", cargarHorarios);
    zonaInput.addEventListener("change", cargarHorarios);

    // ===============================
    // ENVÍO DEL FORMULARIO
    // ===============================
    form.addEventListener("submit", (e) => {
        if (!horaHidden.value) {
            e.preventDefault();
            alert("Debes seleccionar un horario.");
        }
    });

});
