package com.ElSauce.demo.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.ElSauce.demo.Enum.EstadoPago;
import com.ElSauce.demo.model.Mesa;
import com.ElSauce.demo.model.Pago;
import com.ElSauce.demo.model.Reserva;
import com.ElSauce.demo.model.User;
import com.ElSauce.demo.model.Zona;
import com.ElSauce.demo.service.PagoService;
import com.ElSauce.demo.service.ReservaService;
import com.ElSauce.demo.service.ZonaService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private ZonaService zonaService;

    @GetMapping("/reserva")
    public String paginaReserva(Model model, HttpSession session) {
        User usuario = (User) session.getAttribute("usuarioLogeado");
        if (usuario == null) {
            return "redirect:/";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("reserva", new Reserva());
        return "reserva";
    }
    
    // ReservaController.java

// ReservaController.java

@PostMapping("/reserva")
public String postMethodName(@ModelAttribute Reserva reserva,
                             @RequestParam("fechaReserva") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaReserva,
                             @RequestParam("horaReserva") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaReservaStr, 
                             @RequestParam("zonaId") Integer zonaId,@RequestParam(value = "redirect", required = false, defaultValue = "index") String redirectTarget,
                             HttpSession session) {

    LocalDateTime horaActual = LocalDateTime.now();
    
    // 1. OBTENCIÓN Y PREPARACIÓN DE DATOS BASE
    User user = (User) session.getAttribute("usuarioLogeado");
    if (user == null) {
        return "redirect:/"; // Asegura que el usuario esté logeado
    }
    
    
    
    // Obtener Zona y Mesa (Mesa sigue siendo placeholder)
    Zona zona = zonaService.buscarZonaPorID(zonaId)
           .orElseThrow(() -> new RuntimeException("Zona no encontrada"));
    
    Mesa mesa = new Mesa(); 
    mesa.setId(1); // Usamos Long para el ID de Mesa
    
    // 2. ASIGNACIÓN DE DATOS A LA RESERVA
    reserva.setUser(user);
    reserva.setMesa(mesa); 
    reserva.setZona(zona);
    reserva.setFechaReserva(fechaReserva); 
    reserva.setHoraReserva(horaReservaStr); 
    reserva.setCreatedAt(horaActual);

    // Los demás campos del cliente se mapean por @ModelAttribute
    
    // 3. CALCULAR EL PRECIO DE LA RESERVA
    double precio = 0;
    // La zona ya está asignada, podemos usar reserva.getZona()
    switch (reserva.getZona().getNombre()) {
        case "Muelle Panorámico":
            precio = reserva.getPersonas() * 8.00;
            break;
        case "Mirador Azul":
            precio = reserva.getPersonas() * 9.00;
            break;
        case "Salón Bosque":
            precio = reserva.getPersonas() * 10.00;
            break;
        default:
            // Define un precio base o maneja la excepción/default
            precio = reserva.getPersonas() * 7.00; 
            break;
    }

    // 4. GUARDAR LA RESERVA EN LA BD
    Reserva reservaGuardada = reservaService.guardarReserva(reserva);
    
    // 5. CREAR Y GUARDAR EL PAGO (CORRECCIÓN DE ORDEN)
    Pago pago = new Pago();
    
    // Generación de datos de Pago
    LocalDateTime fechaActual = LocalDateTime.now();
    SecureRandom random = new SecureRandom();
    String idTransaccion = "PAY-" + (100000 + random.nextInt(900000));
    
    // Asignación de datos al Pago
    pago.setReserva(reservaGuardada); // Usa la reserva que ya tiene ID
    pago.setFechaTransaccion(fechaActual);
    pago.setIdTransaccion(idTransaccion);
    pago.setMetodoPago("Tarjeta");
    pago.setMonto(BigDecimal.valueOf(precio).setScale(2, RoundingMode.HALF_UP)); // <--- ¡ASIGNACIÓN DE MONTO ANTES DE GUARDAR!
    pago.setEstadoPago(EstadoPago.PAID); // Asume pago exitoso

    // Guardar el Pago en la BD
    pagoService.guardarPago(pago);
    
    System.out.println("Reserva y Pago exitosos. Zona: " + reserva.getZona().getNombre());
    if ("dashboard-reservas".equalsIgnoreCase(redirectTarget)) {
            // Si viene del dashboard, redirige a /reserva para recargar la lista
            return "redirect:/dashboard-reservas";
        } else {
            // Por defecto, o si viene de reserva.html, redirige a /index
            return "redirect:/index"; 
        }
}
    
}
