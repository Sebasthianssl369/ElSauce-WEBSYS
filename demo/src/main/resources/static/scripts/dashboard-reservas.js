
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


    // Abrir lista por defecto
    showSection("list");

    // ===============================
    //   ELEMENTOS DE RESERVA
    // ===============================
    const personas = document.getElementById("personas");
    const fecha = document.getElementById("fechaReserva");
    const zona = document.getElementById("zona");
    const horaHidden = document.getElementById("horaReserva");
    const horariosContainer = document.getElementById("horariosContainer");
    const montoInput = document.getElementById("monto");

    if (!personas || !fecha || !zona || !horariosContainer || !montoInput) {
        console.error("Faltan elementos importantes en el DOM");
        return;
    }

    // ===============================
    //   FUNCION CALCULAR MONTO
    // ===============================
    function calcularMonto() {
        const personasNum = Number(personas.value) || 0;
        const zonaNombre = zona.options[zona.selectedIndex]?.text || "";
        let precioUnit = 7.00;

        switch (zonaNombre) {
            case "Muelle Panorámico": precioUnit = 8.00; break;
            case "Mirador Azul": precioUnit = 9.00; break;
            case "Salón Bosque": precioUnit = 10.00; break;
        }

        const monto = personasNum * precioUnit;
        montoInput.value = monto.toFixed(2);
    }

    personas.addEventListener("input", calcularMonto);
    zona.addEventListener("change", calcularMonto);

    // ===============================
    //   FUNCIONES DE HORARIOS
    // ===============================
    function renderHorarios(lista) {
        horariosContainer.innerHTML = "";

        if (!lista || lista.length === 0) {
            horariosContainer.innerHTML = `<p class="text-danger">No hay horarios disponibles.</p>`;
            return;
        }

        lista.forEach(h => {
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
        if (!personas.value || !fecha.value || !zona.value) {
            horariosContainer.innerHTML = `<p class="text-muted">Selecciona primero personas, zona y fecha.</p>`;
            return;
        }

        horariosContainer.innerHTML = `<p>Cargando...</p>`;

        const url = `/api/horarios/disponibles?personas=${personas.value}&fechaReserva=${fecha.value}&zona=${zona.value}`;

        try {
            const resp = await fetch(url);
            const data = await resp.json();
            console.log("Horarios disponibles:", data);
            renderHorarios(data);
        } catch (error) {
            console.error("Error cargando horarios:", error);
            horariosContainer.innerHTML = `<p class="text-danger">Error cargando horarios.</p>`;
        }
    }

    personas.addEventListener("change", cargarHorarios);
    fecha.addEventListener("change", cargarHorarios);
    zona.addEventListener("change", cargarHorarios);

    // ===============================
    //   ENVÍO DEL FORMULARIO
    // ===============================
    const form = document.getElementById("formAgregarReserva");
    form.addEventListener("submit", (e) => {
        if (!horaHidden.value) {
            e.preventDefault();
            alert("Debes seleccionar una hora para la reserva.");
        }
    });

});

