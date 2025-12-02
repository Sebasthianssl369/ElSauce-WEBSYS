/* ======================================================
    reserva.js - Wizard completo con pasos 1 a 6 (CON FILTRO DE HORA ACTUAL)
    ====================================================== */

document.addEventListener("DOMContentLoaded", () => { // 👈 Inicio de DOMContentLoaded

    // ----------------------------
    // 1) ELEMENTOS Y ESTADO GLOBAL
    // ----------------------------
    const steps = Array.from(document.querySelectorAll(".reserva-step"));
    const timelineSteps = Array.from(document.querySelectorAll(".timeline .step"));
    const horaGrid = document.getElementById("hora-grid");
    const inputPersonas = document.getElementById("personas");
    const slides = Array.from(document.querySelectorAll(".zona-slide"));
    const resumenDiv = document.getElementById("resumen-reserva");
    const resumenFinal = document.getElementById("resumen-final");
    
    // Elementos del Paso 6 (para funciones internas)
    const numeroTarjeta = document.getElementById("numero-tarjeta");
    const cvv = document.getElementById("cvv");
    const fechaExp = document.getElementById("exp-fecha");
    const nombreTarjeta = document.getElementById("nombre-tarjeta");
    const btnFinalizarReserva = document.getElementById("btn-confirmar");
    const formDatos = document.getElementById("form-datos");
    const btnValidarDatos = document.getElementById("btn-validar-datos"); 
    const emailInput = document.getElementById("email");
    const telefonoInput = document.getElementById("telefono");
    const dniInput = document.getElementById("dni"); 
    const tipoComprobante = document.getElementById("tipo-comprobante");
    const boletaCampos = document.getElementById("boleta-campos");
    const facturaCampos = document.getElementById("factura-campos");
    
    const MAX_PERSONAS = 8;
    
    let reservaActiva = true; // Para el aviso de salida
    let selectedDate = null;
    let selectedCell = null;
    let selectedHourCell = null;
    let selectedZona = null;

    const reservaData = { 
        personas: 2, 
        fecha: null, 
        hora: null, 
        zona: null,
        nombre: null,
        apellidos: null,
        email: null,
        telefono: null,
        numeroDocumento: null,
        monto: 0.00
    };

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

            // Validaciones
            if (current === 0) { 
                if (parseInt(reservaData.personas) < 2) {
                    alert("❌ ERROR: El mínimo es 2 personas.");
                    canAdvance = false;
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
    if (inputPersonas) {
        // Inicialización
        let initialValue = parseInt(inputPersonas.value);
        if (isNaN(initialValue) || initialValue < 2) initialValue = 2;
        else if (initialValue > MAX_PERSONAS) initialValue = MAX_PERSONAS;
        inputPersonas.value = initialValue;
        reservaData.personas = initialValue;

        inputPersonas.addEventListener("input", () => {
            let valorActual = parseInt(inputPersonas.value);
            
            if (valorActual > MAX_PERSONAS) {
                alert(`⛔ La reserva máxima permitida para este formulario es de ${MAX_PERSONAS} personas.`);
                inputPersonas.value = MAX_PERSONAS;
                valorActual = MAX_PERSONAS;
            }
            if (valorActual < 2) {
                 inputPersonas.value = 2;
                 valorActual = 2;
            }

            reservaData.personas = valorActual;
            actualizarResumen();
            cargarHorariosDisponibles(); 
        });
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

            ["Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"].forEach(d => {
                const el = document.createElement("div");
                el.classList.add("day-name");
                el.textContent = d;
                calendar.appendChild(el);
            });

            let firstDay = new Date(year, month, 1).getDay(); 
            const offset = (firstDay === 0) ? 6 : firstDay - 1; 
            const lastDate = new Date(year, month + 1, 0).getDate();
            
            for (let i = 0; i < offset; i++) calendar.appendChild(document.createElement("div"));

            for (let day = 1; day <= lastDate; day++) {
                const cell = document.createElement("div");
                cell.classList.add("day");
                cell.textContent = day;
                
                const cellDate = new Date(year, month, day);
                cellDate.setHours(0, 0, 0, 0);

                if (cellDate.getTime() < today.getTime()) {
                    cell.classList.add("disabled");
                } else if (cellDate.getTime() === today.getTime()) {
                    cell.classList.add("today");
                }

                if (selectedDate && cellDate.toDateString() === selectedDate.toDateString()) {
                    cell.classList.add("selected");
                    selectedCell = cell;
                }

                cell.addEventListener("click", () => {
                    if (cell.classList.contains("disabled")) return; 

                    if (selectedCell) selectedCell.classList.remove("selected");
                    cell.classList.add("selected");
                    selectedCell = cell;
                    
                    selectedDate = cellDate;
                    reservaData.fecha = cellDate;
                    
                    const inputFecha = document.getElementById("inputFecha");
                    if(inputFecha) inputFecha.value = cellDate.toISOString().split("T")[0];
                    
                    actualizarResumen();
                    
                    // Desseleccionar hora y recargar al cambiar la fecha
                    reservaData.hora = null;
                    if (selectedHourCell) {
                        selectedHourCell.classList.remove("selected");
                        selectedHourCell = null;
                    }
                    cargarHorariosDisponibles();
                });

                calendar.appendChild(cell);
            }

            calContainer.appendChild(calendar);
        }

        prevBtn.addEventListener("click", () => {
            if (currentYear === today.getFullYear() && currentMonth === today.getMonth()) return;
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
                const inputZona = document.getElementById("inputZona");
                if (inputZona) inputZona.value = zonaId;

                actualizarResumen();
                
                // Desseleccionar hora y recargar al cambiar la zona
                reservaData.hora = null;
                if (selectedHourCell) {
                    selectedHourCell.classList.remove("selected");
                    selectedHourCell = null;
                }
                cargarHorariosDisponibles();
            });
        });
    }

    // ----------------------------
    // 5) PASO 3: Hora (LÓGICA DE CARGA Y FILTRADO)
    // ----------------------------

    function renderHorarios(horarios) {
        if (!horaGrid) return;
        horaGrid.innerHTML = ''; 

        if (horarios.length === 0) {
            horaGrid.innerHTML = `<p class="text-danger">No hay horarios disponibles.</p>`;
            return;
        }

        horarios.forEach(hora24h => {
            const [h, m] = hora24h.split(':');
            const date = new Date(null, null, null, h, m);
            const horaDisplay = date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: true });

            const cell = document.createElement("div");
            cell.classList.add("hora-slot");
            cell.textContent = horaDisplay;
            cell.setAttribute('data-hora-24h', `${h.padStart(2, '0')}:${m.padStart(2, '0')}`); // "HH:mm"

            cell.addEventListener("click", () => {
                if (selectedHourCell) selectedHourCell.classList.remove("selected");
                cell.classList.add("selected");
                selectedHourCell = cell;
                
                const hora24hFormato = cell.getAttribute('data-hora-24h');
                reservaData.hora = hora24hFormato; 
                const inputHora = document.getElementById("inputHora");
                if (inputHora) inputHora.value = hora24hFormato;
                
                actualizarResumen();
            });
            horaGrid.appendChild(cell);
        });
    }

    /**
     * Filtra los horarios para eliminar aquellos que ya pasaron SOLO si la fecha de reserva es HOY.
     */
    function filtrarHorasPasadas(horarios, fechaReserva) {
        const hoy = new Date();
        
        // 1. Compara si la fecha de reserva es HOY (ignorando la hora)
        const esHoy = 
            fechaReserva.getDate() === hoy.getDate() &&
            fechaReserva.getMonth() === hoy.getMonth() &&
            fechaReserva.getFullYear() === hoy.getFullYear();

        if (!esHoy) {
            return horarios; // Si NO es hoy, devuelve la lista completa sin filtrar.
        }

        // 2. Si es hoy, filtra por hora actual + 45 min de margen
        const margenMinutos = 15; 
        const ahoraMinutos = hoy.getHours() * 60 + hoy.getMinutes();
        const umbralMinutos = ahoraMinutos + margenMinutos; 

        return horarios.filter(hora24h => {
            const [h, m] = hora24h.split(':').map(Number);
            const horaMinutos = h * 60 + m;
            return horaMinutos >= umbralMinutos;
        });
    }

    async function cargarHorariosDisponibles() {
        if (!reservaData.fecha || !reservaData.zona || !reservaData.personas) {
            renderHorarios([]); 
            return;
        }
        
        const fechaISO = reservaData.fecha.toISOString().split("T")[0];
        const zonaId = reservaData.zona.id;
        const personas = reservaData.personas;

        try {
            horaGrid.innerHTML = `<p>Cargando horarios...</p>`;
            
            const url =
                `/api/horarios/disponibles?fechaReserva=${fechaISO}&zona=${zonaId}&personas=${personas}`;

            const response = await fetch(url);
            if (!response.ok) throw new Error("No se pudo cargar horarios disponibles");

            const horariosDisponibles = await response.json(); 
            const horariosNormalizados = horariosDisponibles.map(h => h.substring(0, 5));

            // Aplicar el filtro de horas pasadas (solo si es hoy)
            const horariosFinales = filtrarHorasPasadas(horariosNormalizados, reservaData.fecha);

            renderHorarios(horariosFinales);

        } catch (err) {
            console.error("Error filtrando o cargando horarios:", err);
            horaGrid.innerHTML = `<p class="text-danger">Error cargando horarios disponibles.</p>`;
        }
    }


    async function fetchHorarios() {
        if (!horaGrid) return;
        
        // Cargar horarios iniciales (sin disponibilidad, solo los base)
        try {
            const response = await fetch('/api/horarios'); 
            const data = await response.json(); 
            // Filtro inicial solo por hora actual
            const horariosBase = data.map(h => h.substring(0, 5));
            const horariosIniciales = filtrarHorasPasadas(horariosBase, new Date());
            renderHorarios(horariosIniciales);
            
            // Intenta cargar los disponibles si ya hay datos de reserva
            cargarHorariosDisponibles(); 

        } catch (error) {
            console.error('Fallo en fetchHorarios. Usando horarios estáticos de respaldo.', error);
            const horariosRespaldo = ["12:00:00", "13:30:00", "15:00:00", "19:00:00", "20:30:00", "21:00:00"];
            const horariosFiltradosRespaldo = filtrarHorasPasadas(horariosRespaldo, new Date());
            renderHorarios(horariosFiltradosRespaldo); 
        }
    }

    if (horaGrid) {
        fetchHorarios();
    } // 👈 ¡Aquí faltaba la llave de cierre del if(horaGrid)!


    // ----------------------------
    // 7) PASO 5: Datos del cliente + resumen
    // ----------------------------
    
    if (emailInput) {
        emailInput.addEventListener("input", () => {
            const value = emailInput.value.trim();
            emailInput.setCustomValidity(
                !value.endsWith("@gmail.com") && value !== "" 
                ? "Solo se permiten correos @gmail.com" 
                : ""
            );
        });
    }

    if (dniInput) {
        dniInput.addEventListener("input", () => {
            dniInput.value = dniInput.value.replace(/\D/g, "").slice(0, 8);
        });
        dniInput.addEventListener("blur", () => {
            dniInput.setCustomValidity(dniInput.value.length !== 8 ? "El DNI debe tener 8 dígitos" : "");
        });
    }

    if (telefonoInput) {
        telefonoInput.value = "+51 ";
        telefonoInput.addEventListener("focus", () => {
            if (!telefonoInput.value.startsWith("+51 ")) telefonoInput.value = "+51 ";
        });
        telefonoInput.addEventListener("input", () => {
            if (!telefonoInput.value.startsWith("+51 ")) telefonoInput.value = "+51 " + telefonoInput.value.replace(/\D/g, "");
            const soloNumeros = telefonoInput.value.replace("+51", "").replace(/\D/g, "");
            if (soloNumeros.length > 9) telefonoInput.value = "+51 " + soloNumeros.slice(0, 9);
        });
        telefonoInput.addEventListener("blur", () => {
            const soloNumeros = telefonoInput.value.replace("+51", "").replace(/\D/g, "");
            telefonoInput.setCustomValidity(soloNumeros.length !== 9 ? "El número debe tener 9 dígitos" : "");
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
            
            reservaData.nombre = document.getElementById("nombre").value.trim();
            reservaData.apellidos = document.getElementById("apellidos").value.trim();
            reservaData.email = document.getElementById("email").value.trim();
            reservaData.telefono = document.getElementById("telefono").value.trim();
            reservaData.numeroDocumento = dniInput ? dniInput.value.trim() : null;

            const privacidad = document.getElementById("acepto-terminos");
            if (!privacidad || !privacidad.checked) {
                alert("🔒 Debes aceptar los términos y condiciones para continuar.");
                return;
            }

            showStep(5); // ✅ Avanza al Paso 6
        });
    }


    // ----------------------------
    // 8) PASO 6: Confirmar tarjeta + resumen final
    // ----------------------------
    
    if (numeroTarjeta) numeroTarjeta.addEventListener("input", () => numeroTarjeta.value = numeroTarjeta.value.replace(/\D/g, "").slice(0, 16));
    if (cvv) cvv.addEventListener("input", () => cvv.value = cvv.value.replace(/\D/g, "").slice(0, 4));
    
    if (fechaExp) {
        fechaExp.addEventListener("input", () => {
            fechaExp.value = fechaExp.value.replace(/[^\d\/]/g, "");
            if (fechaExp.value.length === 2 && !fechaExp.value.includes("/")) fechaExp.value = fechaExp.value + "/";
        });
        fechaExp.addEventListener("blur", () => {
            const val = fechaExp.value;
            if (!/^\d{2}\/\d{2}$/.test(val)) return fechaExp.setCustomValidity("Formato inválido (MM/YY)");
            const [mm, yy] = val.split("/").map(Number);
            const now = new Date();
            const currentYear = now.getFullYear() % 100;
            const currentMonth = now.getMonth() + 1;
            if (mm < 1 || mm > 12 || yy < currentYear || (yy === currentYear && mm < currentMonth)) {
                return fechaExp.setCustomValidity("Fecha inválida o vencida");
            }
            fechaExp.setCustomValidity("");
        });
    }

    function calcularMonto() {
        const personasNum = Number(reservaData.personas) || 0;
        let precioUnit = 7.00; 
        const zonaNombre = reservaData.zona?.nombre || "";
        
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

    function actualizarResumen() {
        const fechaStr = reservaData.fecha ? reservaData.fecha.toLocaleDateString() : "No elegida";
        const horaDisplay = reservaData.hora || "No elegida";
        const zonaNombre = reservaData.zona?.nombre || "No seleccionada";
        const monto = calcularMonto();

        const resumenHTML = `
            <p><strong>Personas:</strong> ${reservaData.personas || ""}</p>
            <p><strong>Fecha:</strong> ${fechaStr}</p>
            <p><strong>Hora:</strong> ${horaDisplay}</p>
            <p><strong>Zona:</strong> ${zonaNombre}</p>
            <p><strong>Monto a pagar:</strong> S/ ${monto.toFixed(2)}</p>
        `;

        if (resumenDiv) resumenDiv.innerHTML = `<h3>Tu Reserva</h3>` + resumenHTML;

        if (resumenFinal) {
            resumenFinal.innerHTML = `
                <h3>Resumen de la reserva</h3>
                <p><strong>Cliente:</strong> ${reservaData.nombre || ""} ${reservaData.apellidos || ""}</p>
                <p><strong>Email:</strong> ${reservaData.email || ""}</p>
                ${reservaData.numeroDocumento ? `<p><strong>DNI:</strong> ${reservaData.numeroDocumento}</p>` : ''}
                <hr style="border-top: 1px solid #eee;">
                ${resumenHTML}
            `;
        }
    }

    if (btnFinalizarReserva) {
        btnFinalizarReserva.addEventListener("click", async (e) => {
            e.preventDefault();

            if (!nombreTarjeta?.value || !numeroTarjeta?.value || !cvv?.value || !fechaExp?.value) {
                alert("Por favor completa todos los datos de la tarjeta.");
                return;
            }
            
            // Simulación de validación de formulario del paso 5 si se saltó o regresó
            if (formDatos && !formDatos.checkValidity()) {
                 formDatos.reportValidity();
                 showStep(4);
                 return;
            }

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
            formData.append("monto", reservaData.monto);

            try {
                const response = await fetch("/reserva", {
                    method: "POST",
                    body: new URLSearchParams(formData)
                });

                if (response.ok) {
                    alert(`✅ ¡Reserva confirmada! Monto pagado: S/ ${reservaData.monto.toFixed(2)}`);
                    reservaActiva = false; 
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

    // ----------------------------
    // 9) PREVENCIÓN DE SALIDA
    // ----------------------------
    const contenedorReserva = document.querySelector(".reserva-container");
    if (contenedorReserva) {
        document.querySelectorAll("nav a").forEach(link => {
            link.addEventListener("click", function(e){
                if (!reservaActiva) return; 
                const confirmar = confirm("¿Estás seguro que deseas salir? Los datos de la reserva no se guardarán.");
                if (!confirmar) {
                    e.preventDefault();
                    return;
                }
                reservaActiva = false;
            });
        });
    }

    // Inicialización: Muestra el primer paso al cargar
    showStep(0);
}); // 👈 ¡Fin de DOMContentLoaded!