/* ======================================================
    reserva.js - Wizard completo con pasos 1 a 6 (con monto)
    ====================================================== */
document.addEventListener("DOMContentLoaded", () => {
    // ----------------------------
    // 1) ELEMENTOS Y ESTADO GLOBAL
    // ----------------------------
    const steps = Array.from(document.querySelectorAll(".reserva-step"));
    const timelineSteps = Array.from(document.querySelectorAll(".timeline .step"));

    const reservaData = { 
        personas: 2, 
        fecha: null, 
        hora: null, // Guardará la hora en formato "HH:mm" (24h)
        zona: null,
        nombre: null,
        apellidos: null,
        email: null,
        telefono: null,
        numeroDocumento: null,
        monto: 0.00
    };

    let selectedDate = null;
    let selectedCell = null;
    let selectedHourCell = null;
    let selectedZona = null;

    const resumenDiv = document.getElementById("resumen-reserva");
    const resumenFinal = document.getElementById("resumen-final");

    // ----------------------------
    // 2) FUNCIONES DE NAVEGACIÓN
    // ----------------------------
    function getActiveStepIndex() {
        return steps.findIndex(s => s.classList.contains("active"));
    }

    function showStep(index) {
        steps.forEach((s, i) => s.classList.toggle("active", i === index));
        
        timelineSteps.forEach((t, i) => {
            t.classList.toggle("active", i === index);
            t.classList.toggle("completed", i < index);
        });
        
        // Si avanzamos al paso 5 (index 4) o más, actualizamos el resumen
        if (index >= 4) actualizarResumen(); 
    }

    document.addEventListener("click", (e) => {
        const btnAnterior = e.target.closest(".btn-anterior");
        const btnSiguiente = e.target.closest(".btn-siguiente");
        
        const current = getActiveStepIndex();
        
        if (btnAnterior) {
            e.preventDefault();
            if (current > 0) showStep(current - 1);
            return;
        }

        if (btnSiguiente && btnSiguiente.id !== 'btn-validar-datos') {
            e.preventDefault();
            
            let canAdvance = true;

            if (current === 0) { 
                const personas = parseInt(reservaData.personas);
                if (isNaN(personas) || personas < 2) {
                    alert("❌ ERROR: El mínimo es 2 personas.");
                    canAdvance = false;
                } else if (personas > 8) { // Validamos que el límite no se haya excedido
                    alert(`⛔ No se permiten reservas de más de 8 personas.`);
                    canAdvance = false; // ¡IMPORTANTE! Impedimos el avance
                }
            } else if (current === 1) { 
                if (!reservaData.fecha) {
                    alert("📅 Seleccione una fecha.");
                    canAdvance = false;
                }
            } else if (current === 2) { 
                if (!reservaData.zona) {
                    alert("📍 Seleccione una zona.");
                    canAdvance = false;
                }
            } else if (current === 3) { 
                if (!reservaData.hora) {
                    alert("⚠️ Seleccione un horario.");
                    canAdvance = false;
                }
            }

            if (canAdvance && current < steps.length - 1) {
                showStep(current + 1);
            }
        }
    });

    // ----------------------------
    // 3) PASO 1: Personas
    // ----------------------------
    const MAX_PERSONAS = 8; // Define el máximo aquí para fácil referencia

const inputPersonas = document.getElementById("personas");
if (inputPersonas) {
    inputPersonas.addEventListener("input", () => {
        let valorActual = parseInt(inputPersonas.value);
        
        // 1. Manejar la restricción máxima
        if (valorActual > MAX_PERSONAS) {
            alert(`⛔ La reserva máxima permitida para este formulario es de ${MAX_PERSONAS} personas. Si necesita más, contáctenos.`);
            // Restablece el valor en el campo de entrada a 8
            inputPersonas.value = MAX_PERSONAS;
            valorActual = MAX_PERSONAS;
        }

        // 2. Manejar la restricción mínima (opcional, si el input no lo hace)
        if (valorActual < 2) {
             inputPersonas.value = 2;
             valorActual = 2;
        }

        // 3. Guardar el valor válido
        reservaData.personas = valorActual;
        actualizarResumen();
    });
    
    // Inicialización del dato al cargar
    // Asegura que el valor inicial también respete el límite
    let initialValue = parseInt(inputPersonas.value);
    if (isNaN(initialValue) || initialValue < 2) {
        initialValue = 2;
    } else if (initialValue > MAX_PERSONAS) {
        initialValue = MAX_PERSONAS;
    }
    inputPersonas.value = initialValue;
    reservaData.personas = initialValue;
}

    // ----------------------------
    // 4) PASO 2: Calendario
    // ----------------------------
    const calContainer = document.getElementById("calendar-container");
    const prevBtn = document.querySelector(".cal-nav.prev");
    const nextBtn = document.querySelector(".cal-nav.next");

    if (calContainer && prevBtn && nextBtn) {
        let currentMonth = new Date().getMonth();
        let currentYear = new Date().getFullYear();
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        function renderCalendar(month, year) {
            calContainer.innerHTML = "";
            const calendar = document.createElement("div");
            calendar.classList.add("calendar");

            const monthName = new Date(year, month).toLocaleString("es-ES", { month: "long", year: "numeric" });
            const header = document.createElement("div");
            header.classList.add("calendar-header");
            header.textContent = monthName.charAt(0).toUpperCase() + monthName.slice(1);
            calendar.appendChild(header);

            // Encabezados de días (Lunes a Domingo)
            ["Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"].forEach(d => {
                const el = document.createElement("div");
                el.classList.add("day-name");
                el.textContent = d;
                calendar.appendChild(el);
            });

            // CÁLCULO DE OFFSET CORREGIDO: Lunes como primer día
            let firstDay = new Date(year, month, 1).getDay(); // 0=Dom, 1=Lun, ..., 6=Sáb
            const offset = (firstDay === 0) ? 6 : firstDay - 1; 
            const lastDate = new Date(year, month + 1, 0).getDate();
            
            // Agregar celdas vacías al inicio
            for (let i = 0; i < offset; i++) calendar.appendChild(document.createElement("div"));

            for (let day = 1; day <= lastDate; day++) {
                const cell = document.createElement("div");
                cell.classList.add("day");
                cell.textContent = day;
                
                const cellDate = new Date(year, month, day);
                cellDate.setHours(0, 0, 0, 0);

                // Días pasados
                if (cellDate.getTime() < today.getTime()) {
                    cell.classList.add("disabled");
                } 
                // Día actual (hoy)
                else if (cellDate.getTime() === today.getTime()) {
                    cell.classList.add("today");
                }

                // Si es la fecha previamente seleccionada, marcarla
                if (selectedDate && cellDate.toDateString() === selectedDate.toDateString()) {
                    cell.classList.add("selected");
                    selectedCell = cell;
                }

                // Listener de selección
                cell.addEventListener("click", () => {
                    if (cell.classList.contains("disabled")) return; 

                    if (selectedCell) selectedCell.classList.remove("selected");
                    
                    cell.classList.add("selected");
                    selectedCell = cell;
                    
                    selectedDate = cellDate;
                    reservaData.fecha = cellDate;
                    document.getElementById("inputFecha").value = cellDate.toISOString().split("T")[0];
                    actualizarResumen();
                });

                calendar.appendChild(cell);
            }

            calContainer.appendChild(calendar);
        }

        // --- NAVEGACIÓN ENTRE MESES ---
        prevBtn.addEventListener("click", () => {
            if (currentYear === today.getFullYear() && currentMonth === today.getMonth()) {
                return;
            }
            currentMonth--;
            if (currentMonth < 0) { currentMonth = 11; currentYear--; }
            renderCalendar(currentMonth, currentYear);
        });

        nextBtn.addEventListener("click", () => {
            currentMonth++;
            if (currentMonth > 11) { currentMonth = 0; currentYear++; }
            renderCalendar(currentMonth, currentYear);
        });

        renderCalendar(currentMonth, currentYear);
    }

    // ----------------------------
    // 6) PASO 4: Zona
    // ----------------------------

const slides = Array.from(document.querySelectorAll(".zona-slide"));
const slidesContainer = document.querySelector(".zona-slides");
const prevZona = document.querySelector(".zona-nav.prev");
const nextZona = document.querySelector(".zona-nav.next");



if (slides.length && slidesContainer && prevZona && nextZona) {
    let currentZonaIndex = 0;

    function updateZonaPosition() {
        slidesContainer.style.transform = `translateX(-${currentZonaIndex * 100}%)`;
    }

    prevZona.addEventListener("click", () => {
        currentZonaIndex = (currentZonaIndex - 1 + slides.length) % slides.length;
        updateZonaPosition();
    });

    nextZona.addEventListener("click", () => {
        currentZonaIndex = (currentZonaIndex + 1) % slides.length;
        updateZonaPosition();
    });

    slides.forEach(slide => {
        slide.addEventListener("click", () => {
            slides.forEach(s => s.classList.remove("selected"));
            slide.classList.add("selected");

            const zonaId = slide.dataset.id;
            const zonaNombre = slide.dataset.zona;

            selectedZona = { id: zonaId, nombre: zonaNombre };
            reservaData.zona = selectedZona;
            document.getElementById("inputZona").value = zonaId;

            actualizarResumen();

            // ⬇️ MOVIDO AQUÍ (correcto)
            setTimeout(cargarHorariosDisponibles, 50);
        });
    });
}

    // ----------------------------
    // 5) PASO 3: Hora (CARGA DINÁMICA DE BD)
    // ----------------------------
    const horaGrid = document.getElementById("hora-grid");

    function renderHorarios(horarios) {
        if (!horaGrid) return;
        horaGrid.innerHTML = ''; // Limpia el grid existente

        horarios.forEach(hora24h => {
            // Conversión de formato (De "HH:mm:ss" a "HH:MM AM/PM")
            const [h, m] = hora24h.split(':');
            const date = new Date(null, null, null, h, m);
            
            // Formato AM/PM para la interfaz
            const horaDisplay = date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: true });

            const cell = document.createElement("div");
            cell.classList.add("hora-slot");
            cell.textContent = horaDisplay;
            cell.setAttribute('data-hora-24h', `${h.padStart(2, '0')}:${m.padStart(2, '0')}`); // "HH:mm"

            cell.addEventListener("click", () => {
                if (selectedHourCell) selectedHourCell.classList.remove("selected");
                cell.classList.add("selected");
                selectedHourCell = cell;
                
                // Guarda la hora en formato 24h para el backend (HH:mm)
                const hora24hFormato = cell.getAttribute('data-hora-24h');
                reservaData.hora = hora24hFormato; 
                document.getElementById("inputHora").value = hora24hFormato;
                actualizarResumen();
            });
            horaGrid.appendChild(cell);
        });
    }

       async function fetchHorarios() {
        if (!horaGrid) return;

        try {
            const response = await fetch('/api/horarios'); 
            if (!response.ok) {
                throw new Error('Error al cargar los horarios: ' + response.statusText);
            }
            const data = await response.json(); 
            renderHorarios(data);
        } catch (error) {
            console.error('Fallo en fetchHorarios. Usando horarios estáticos de respaldo.', error);
            const horariosRespaldo = ["12:00:00", "13:30:00", "15:00:00", "19:00:00", "20:30:00", "21:00:00"];
            renderHorarios(horariosRespaldo); 
        }
    }

    if (horaGrid) {
        fetchHorarios();
    }

    async function cargarHorariosDisponibles() {
    if (!reservaData.fecha || !reservaData.zona) return;

    const fechaISO = reservaData.fecha.toISOString().split("T")[0];
    const zonaId = reservaData.zona.id;
    const personas = reservaData.personas;

    try {
        const url =
            `/api/horarios/disponibles?fechaReserva=${fechaISO}&zona=${zonaId}&personas=${personas}`;

        const response = await fetch(url);
        if (!response.ok) throw new Error("No se pudo cargar horarios disponibles");

        const horariosDisponibles = await response.json(); // Ej: ["12:00:00","13:00:00"]

        // Renderizar solo horarios disponibles
        renderHorarios(horariosDisponibles);

    } catch (err) {
        console.error("Error filtrando horarios:", err);
    }
}

// Cuando cambie PERSONAS → recalcular horarios
if (inputPersonas) {
    inputPersonas.addEventListener("change", () => {
        cargarHorariosDisponibles();
    });
}

// Cuando se seleccione FECHA → recalcular horarios
document.addEventListener("click", (e) => {
    if (e.target.classList.contains("day")) {
        setTimeout(cargarHorariosDisponibles, 50);
    }
});

// Cuando se seleccione ZONA → recalcular horarios
slides.forEach(slide => {
    slide.addEventListener("click", () => {
        setTimeout(cargarHorariosDisponibles, 50);
    });
});

    // ----------------------------
    // 7) PASO 5: Datos del cliente + resumen
    // ----------------------------
    const formDatos = document.getElementById("form-datos");
    const btnValidarDatos = document.getElementById("btn-validar-datos"); 
    const emailInput = document.getElementById("email");
    const telefonoInput = document.getElementById("telefono");
    const dniInput = document.getElementById("dni"); 
    const tipoComprobante = document.getElementById("tipo-comprobante");
    const boletaCampos = document.getElementById("boleta-campos");
    const facturaCampos = document.getElementById("factura-campos");

    if (emailInput) {
        emailInput.addEventListener("input", () => {
            const value = emailInput.value.trim();
            if (!value.endsWith("@gmail.com") && value !== "") {
                emailInput.setCustomValidity("Solo se permiten correos @gmail.com");
            } else {
                emailInput.setCustomValidity("");
            }
        });
    }

    // ----------------------------
    // DNI – SOLO NÚMEROS (8 dígitos exactos)
    // ----------------------------
    if (dniInput) {
        dniInput.addEventListener("input", () => {
            dniInput.value = dniInput.value.replace(/\D/g, "").slice(0, 8);
        });

        dniInput.addEventListener("blur", () => {
            if (dniInput.value.length !== 8) {
                dniInput.setCustomValidity("El DNI debe tener 8 dígitos");
            } else {
                dniInput.setCustomValidity("");
            }
        });
    }

    // ----------------------------
    // Teléfono – +51 + 9 dígitos exactos
    // ----------------------------
    if (telefonoInput) {
        telefonoInput.value = "+51 ";

        telefonoInput.addEventListener("focus", () => {
            if (!telefonoInput.value.startsWith("+51 ")) {
                telefonoInput.value = "+51 ";
            }
        });

        telefonoInput.addEventListener("input", () => {
            if (!telefonoInput.value.startsWith("+51 ")) {
                telefonoInput.value = "+51 " + telefonoInput.value.replace(/\D/g, "");
            }

            const soloNumeros = telefonoInput.value.replace("+51", "").replace(/\D/g, "");

            if (soloNumeros.length > 9) {
                telefonoInput.value = "+51 " + soloNumeros.slice(0, 9);
            }
        });

        telefonoInput.addEventListener("blur", () => {
            const soloNumeros = telefonoInput.value.replace("+51", "").replace(/\D/g, "");
            if (soloNumeros.length !== 9) {
                telefonoInput.setCustomValidity("El número debe tener 9 dígitos");
            } else {
                telefonoInput.setCustomValidity("");
            }
        });
    }

    if (tipoComprobante) {
        tipoComprobante.addEventListener("change", () => {
            const tipo = tipoComprobante.value;
            if (boletaCampos) boletaCampos.classList.toggle("hidden", tipo !== "boleta");
            if (facturaCampos) facturaCampos.classList.toggle("hidden", tipo !== "factura");
        });
    }

    if (btnValidarDatos) {
        btnValidarDatos.addEventListener("click", (e) => {
            e.preventDefault(); 
            
            if (!formDatos.checkValidity()) {
                formDatos.reportValidity(); 
                return; 
            }
            
            // Guardar datos del Paso 5 en el objeto global
            reservaData.nombre = document.getElementById("nombre").value.trim();
            reservaData.apellidos = document.getElementById("apellidos").value.trim();
            reservaData.email = document.getElementById("email").value.trim();
            reservaData.telefono = document.getElementById("telefono").value.trim();
            reservaData.numeroDocumento = dniInput ? dniInput.value.trim() : null;

            // Verifica la casilla de privacidad
            const privacidad = document.getElementById("acepto-terminos");
            if (!privacidad || !privacidad.checked) {
                alert("🔒 Debes aceptar los términos y condiciones para continuar.");
                return;
            }

            // ✅ Si todo es válido, avanza al paso 6
            showStep(5); // Índice del Paso 6
        });
    }

    // ----------------------------
    // 8) PASO 6: Confirmar tarjeta + resumen final (LÓGICA DE ENVÍO AL BACKEND)
    // ----------------------------
    const btnFinalizarReserva = document.getElementById("btn-confirmar");

    // TARJETA – restricciones (número, cvv, expiración)
    const numeroTarjeta = document.getElementById("numero-tarjeta");
    const cvv = document.getElementById("cvv");
    const fechaExp = document.getElementById("exp-fecha");
    const nombreTarjeta = document.getElementById("nombre-tarjeta");

    if (numeroTarjeta) {
        numeroTarjeta.addEventListener("input", () => {
            numeroTarjeta.value = numeroTarjeta.value.replace(/\D/g, "").slice(0, 16);
        });
    }
    if (cvv) {
        cvv.addEventListener("input", () => {
            cvv.value = cvv.value.replace(/\D/g, "").slice(0, 4);
        });
    }
    if (fechaExp) {
        fechaExp.addEventListener("input", () => {
            fechaExp.value = fechaExp.value.replace(/[^\d\/]/g, "");
            if (fechaExp.value.length === 2 && !fechaExp.value.includes("/")) fechaExp.value = fechaExp.value + "/";
        });
        fechaExp.addEventListener("blur", () => {
            const val = fechaExp.value;
            if (!/^\d{2}\/\d{2}$/.test(val)) {
                fechaExp.setCustomValidity("Formato inválido (MM/YY)");
                return;
            }
            const [mm, yy] = val.split("/").map(Number);
            if (mm < 1 || mm > 12) {
                fechaExp.setCustomValidity("Mes inválido");
                return;
            }
            const now = new Date();
            const currentYear = now.getFullYear() % 100;
            const currentMonth = now.getMonth() + 1;
            if (yy < currentYear || (yy === currentYear && mm < currentMonth)) {
                fechaExp.setCustomValidity("La tarjeta está vencida");
                return;
            }
            fechaExp.setCustomValidity("");
        });
    }

    // ----------------------------
    // CÁLCULO DE MONTO (MISMA LÓGICA QUE BACKEND)
    // ----------------------------
    function calcularMonto() {
        const personasNum = Number(reservaData.personas) || 0;
        let precioUnit = 7.00; // default

        const zonaNombre = reservaData.zona && reservaData.zona.nombre ? reservaData.zona.nombre : "";
        switch (zonaNombre) {
            case "Muelle Panorámico": precioUnit = 8.00; break;
            case "Mirador Azul": precioUnit = 9.00; break;
            case "Salón Bosque": precioUnit = 10.00; break;
            default: precioUnit = 7.00; break;
        }

        const monto = personasNum * precioUnit;
        reservaData.monto = Number(monto.toFixed(2));
        return reservaData.monto;
    }

    // ----------------------------
    // ACTUALIZAR RESUMEN (ahora incluye monto)
    // ----------------------------
    function actualizarResumen() {
        const fechaStr = reservaData.fecha ? reservaData.fecha.toLocaleDateString() : "No elegida";
        const horaDisplay = reservaData.hora || "No elegida";
        const zonaNombre = reservaData.zona?.nombre || "No seleccionada";
        const monto = calcularMonto();

        // Resumen lateral (Paso 5)
        if (resumenDiv) {
            resumenDiv.innerHTML = `
                <h3>Tu Reserva</h3>
                <p><strong>Personas:</strong> ${reservaData.personas || ""}</p>
                <p><strong>Fecha:</strong> ${fechaStr}</p>
                <p><strong>Hora:</strong> ${horaDisplay}</p>
                <p><strong>Zona:</strong> ${zonaNombre}</p>
                <p><strong>Monto a pagar:</strong> S/ ${monto.toFixed(2)}</p>
            `;
        }

        // Resumen final (Paso 6)
        if (resumenFinal) {
            resumenFinal.innerHTML = `
                <h3>Resumen de la reserva</h3>
                <p><strong>Cliente:</strong> ${reservaData.nombre || ""} ${reservaData.apellidos || ""}</p>
                <p><strong>Email:</strong> ${reservaData.email || ""}</p>
                ${reservaData.numeroDocumento ? `<p><strong>DNI:</strong> ${reservaData.numeroDocumento}</p>` : ''}
                <hr style="border-top: 1px solid #eee;">
                <p><strong>Personas:</strong> ${reservaData.personas || ""}</p>
                <p><strong>Fecha:</strong> ${fechaStr}</p>
                <p><strong>Hora:</strong> ${horaDisplay}</p>
                <p><strong>Zona:</strong> ${zonaNombre}</p>
                <p><strong>Monto a pagar:</strong> S/ ${monto.toFixed(2)}</p>
            `;
        }
    }

    // Actualiza resumen cuando cambian personas manualmente (por si no hay evento)
    if (inputPersonas) inputPersonas.addEventListener("change", actualizarResumen);

    // ----------------------------
    // BOTÓN FINAL: enviar al backend
    // ----------------------------
    if (btnFinalizarReserva) {
        btnFinalizarReserva.addEventListener("click", async (e) => {
            e.preventDefault();

            // validación tarjeta básica
            if (!nombreTarjeta || !numeroTarjeta || !cvv || !fechaExp) {
                alert("Complete los datos de tarjeta.");
                return;
            }
            if (!nombreTarjeta.value || !numeroTarjeta.value || !cvv.value || !fechaExp.value) {
                alert("Por favor completa todos los datos de la tarjeta.");
                return;
            }

            if (!formDatos.checkValidity()) {
                formDatos.reportValidity();
                return;
            }

            // Prepara envío
            const fechaISO = reservaData.fecha ? reservaData.fecha.toISOString().split("T")[0] : '';

            const formData = new FormData();
            formData.append("clienteNombre", reservaData.nombre);
            formData.append("clienteApellidos", reservaData.apellidos);
            formData.append("clienteEmail", reservaData.email);
            formData.append("clienteTelefono", reservaData.telefono);
            formData.append("numeroDocumento", reservaData.numeroDocumento);
            formData.append("personas", Number(reservaData.personas));
            formData.append("fechaReserva", fechaISO);
            formData.append("horaReserva", reservaData.hora);
            formData.append("zona", reservaData.zona ? reservaData.zona.id : "");
            // opcional: enviar monto al backend si lo deseas
            formData.append("monto", reservaData.monto);

            try {
                const response = await fetch("/reserva", {
                    method: "POST",
                    body: new URLSearchParams(formData)
                });

                if (response.ok) {
                    alert(`✅ ¡Reserva confirmada! Monto pagado (simulado): S/ ${reservaData.monto.toFixed(2)}`);
                    window.location.href = "/";
                } else {
                    const text = await response.text();
                    console.error("Error del servidor:", text);
                    alert("Error al procesar la reserva en el servidor.");
                }
            } catch (err) {
                console.error(err);
                alert("Error de conexión.");
            }
        });
    }

    // Inicialización: Muestra el primer paso al cargar
    showStep(0);
});
