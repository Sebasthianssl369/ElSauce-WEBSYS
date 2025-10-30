/* ======================================================
    reserva.js - Wizard completo con pasos 1 a 6
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
        numeroDocumento: null 
    };

    let selectedDate = null;
    let selectedCell = null;
    let selectedHourCell = null;
    let selectedZona = null;

    const resumenDiv = document.getElementById("resumen-reserva");

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

    // Listener de navegación general (Siguiente/Anterior)
    document.addEventListener("click", (e) => {
        const btnAnterior = e.target.closest(".btn-anterior");
        const btnSiguiente = e.target.closest(".btn-siguiente");
        
        const current = getActiveStepIndex();
        
        if (btnAnterior) {
            e.preventDefault();
            if (current > 0) showStep(current - 1);
            return;
        }

        // Si es un botón "Siguiente" que NO es el del Paso 5 (btn-validar-datos)
        if (btnSiguiente && btnSiguiente.id !== 'btn-validar-datos') {
            e.preventDefault();
            
            // --- VALIDACIONES POR PASO ---
            let canAdvance = true;

            if (current === 0) { // Paso 1: Personas
                const personas = parseInt(reservaData.personas);
                if (personas < 2) {
                    alert("❌ ERROR: El mínimo es 2 personas. Ingrese nuevamente por favor.");
                    canAdvance = false;
                } else if (personas > 8) {
                    alert("⚠️ Nuestro personal se comunicará con usted para reservas grandes.");
                }
            } else if (current === 1) { // Paso 2: Fecha
                if (!reservaData.fecha) {
                    alert("📅 Por favor, seleccione una fecha antes de continuar.");
                    canAdvance = false;
                }
            } else if (current === 2) { // Paso 3: Hora
                if (!reservaData.hora) {
                    alert("⚠️ Por favor, seleccione un horario antes de continuar.");
                    canAdvance = false;
                }
            } else if (current === 3) { // Paso 4: Zona
                if (!reservaData.zona) {
                    alert("📍 Por favor, seleccione una zona antes de continuar.");
                    canAdvance = false;
                }
            }

            // Navegación normal (solo si pasó validación)
            if (canAdvance && current < steps.length - 1) {
                showStep(current + 1);
            }
        }
    });

    // ----------------------------
    // 3) PASO 1: Personas
    // ----------------------------
    const inputPersonas = document.getElementById("personas");
    if (inputPersonas) {
        inputPersonas.addEventListener("input", () => {
            reservaData.personas = inputPersonas.value;
        });
        reservaData.personas = inputPersonas.value;
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

        // --- LLAMADA INICIAL ---
        renderCalendar(currentMonth, currentYear);
    }

    // ----------------------------
    // 5) PASO 3: Hora (CARGA DINÁMICA DE BD)
    // ----------------------------
    const horaGrid = document.getElementById("hora-grid");

    // Función para renderizar y hacer clic en las horas
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
                
                // CORRECCIÓN: Guarda la hora en formato 24h para el backend (HH:mm)
                const hora24hFormato = cell.getAttribute('data-hora-24h');
                reservaData.hora = hora24hFormato; 
                document.getElementById("inputHora").value = hora24hFormato;
                actualizarResumen();
            });
            horaGrid.appendChild(cell);
        });
    }

    // Función principal para obtener los datos de la API
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
            // Horarios estáticos de respaldo si la API falla:
            const horariosRespaldo = ["12:00:00", "13:30:00", "15:00:00", "19:00:00", "20:30:00", "21:00:00"];
            renderHorarios(horariosRespaldo); 
        }
    }

    // Iniciar la carga de horarios si estamos en ese paso
    if (horaGrid) {
        fetchHorarios();
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
            if (slidesContainer) slidesContainer.style.transform = `translateX(-${currentZonaIndex * 100}%)`; 
        }
        prevZona.addEventListener("click", () => { currentZonaIndex = (currentZonaIndex - 1 + slides.length) % slides.length; updateZonaPosition(); });
        nextZona.addEventListener("click", () => { currentZonaIndex = (currentZonaIndex + 1) % slides.length; updateZonaPosition(); });
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
            });
        });
    }

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

    // Lógica de validación de formulario (se mantiene)
    // ...
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

    // Lógica del input de Teléfono (se mantiene)
    if (telefonoInput) {
        telefonoInput.value = "+51 ";
        telefonoInput.addEventListener("focus", () => {
            if (!telefonoInput.value.startsWith("+51")) {
                telefonoInput.value = "+51 ";
            }
        });

        telefonoInput.addEventListener("input", () => {
            if (!telefonoInput.value.startsWith("+51")) {
                telefonoInput.value = "+51 " + telefonoInput.value.replace(/[^0-9]/g, "");
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
    
    // Lógica para mostrar/ocultar campos de Factura/Boleta (se mantiene)
    if (tipoComprobante) {
        tipoComprobante.addEventListener("change", () => {
            const tipo = tipoComprobante.value;
            if (boletaCampos) boletaCampos.classList.toggle("hidden", tipo !== "boleta");
            if (facturaCampos) facturaCampos.classList.toggle("hidden", tipo !== "factura");
        });
    }


    // Listener para el botón 'Siguiente' del Paso 5 (Validación de Datos)
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
    const resumenFinal = document.getElementById("resumen-final");

    function actualizarResumen() {
        const fechaStr = reservaData.fecha ? reservaData.fecha.toLocaleDateString() : "No elegida";
        const horaDisplay = reservaData.hora || "No elegida"; // Muestra la hora 24h o AM/PM según tu preferencia, pero el envío es 24h
        const zonaNombre = reservaData.zona && reservaData.zona.nombre ? reservaData.zona.nombre : "No seleccionada";

        // Resumen lateral (Paso 5)
        if (resumenDiv) {
            resumenDiv.innerHTML = `
                <h3>Tu Reserva</h3>
                <p><strong>Personas:</strong> ${reservaData.personas || ""}</p>
                <p><strong>Fecha:</strong> ${fechaStr}</p>
                <p><strong>Hora:</strong> ${horaDisplay}</p>
                <p><strong>Zona:</strong> ${zonaNombre}</p>
            `;
        }
        
        // Resumen final (Paso 6)
        if (resumenFinal) {
            resumenFinal.innerHTML = `
                <h3>Resumen de la reserva</h3>
                <p><strong>Cliente:</strong> ${reservaData.nombre || ""} ${reservaData.apellidos || ""}</p>
                <p><strong>Email:</strong> ${reservaData.email || ""}</p>
                ${reservaData.numeroDocumento ? `<p><strong>DNI/RUC:</strong> ${reservaData.numeroDocumento}</p>` : ''}
                <hr style="border-top: 1px solid #eee;">
                <p><strong>Personas:</strong> ${reservaData.personas || ""}</p>
                <p><strong>Fecha:</strong> ${fechaStr}</p>
                <p><strong>Hora:</strong> ${horaDisplay}</p>
                <p><strong>Zona:</strong> ${zonaNombre}</p>
            `;
        }
    }

    if (btnFinalizarReserva) {
        btnFinalizarReserva.addEventListener("click", async (e) => { // Agregamos 'async'
            e.preventDefault();

            // Campos de tarjeta (solo validación básica en frontend)
            const nombreTarjeta = document.getElementById("nombre-tarjeta");
            const numeroTarjeta = document.getElementById("numero-tarjeta");
            const cvv = document.getElementById("cvv");
            
            if (!nombreTarjeta.value || !numeroTarjeta.value || !cvv.value) {
                alert("Por favor, completa todos los datos de la tarjeta.");
                return;
            }

            // --- PREPARAR DATOS PARA EL ENVÍO AL BACKEND ---
            const fechaISO = reservaData.fecha ? reservaData.fecha.toISOString().split("T")[0] : ''; // YYYY-MM-DD
            const hora24h = reservaData.hora; // HH:mm

            const formData = new FormData();

            // 1. Campos de Reserva (@ModelAttribute Reserva reserva)
            formData.append('clienteNombre', reservaData.nombre);
            formData.append('clienteApellidos', reservaData.apellidos);
            formData.append('clienteEmail', reservaData.email);
            formData.append('clienteTelefono', reservaData.telefono);
            formData.append('numeroDocumento', reservaData.numeroDocumento);
            formData.append('personas', Number(reservaData.personas));

            // CRUCIAL: Fecha y Hora para el Mapeo de @ModelAttribute Y @RequestParam
            formData.append('fechaReserva', fechaISO); 
            formData.append('horaReserva', hora24h);   
            
            // 2. Parámetro adicional para @RequestParam("zonaId")
            formData.append('zonaId', reservaData.zona.id);
            
            // --- ENVÍO DE DATOS AL BACKEND ---
            try {
                const response = await fetch('/reserva', {
                    method: 'POST',
                    // Importante: URLSearchParams simula el envío de formulario para @ModelAttribute
                    body: new URLSearchParams(formData) 
                });

                if (response.ok) {
                    alert("✅ ¡Reserva confirmada! Guardada en la base de datos.");
                    // Redirige después de que el backend ha guardado y respondido OK
                    window.location.href = "/"; 
                } else {
                    const errorText = await response.text();
                    console.error("Error del servidor:", errorText);
                    alert(`❌ Error al confirmar la reserva. El servidor respondió: ${response.status}. Revisa la consola para detalles.`);
                }
            } catch (error) {
                console.error('Error de red al enviar la reserva:', error);
                alert("❌ Error de conexión al servidor. Intente de nuevo.");
            }
        });
    }

    // Inicialización: Muestra el primer paso al cargar
    showStep(0);
});