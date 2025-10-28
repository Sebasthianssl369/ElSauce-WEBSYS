package com.ElSauce.demo.controller;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

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
    
    @PostMapping("/reserva")
    public String postMethodName(@ModelAttribute Reserva reserva,
                                 @RequestParam("zonaId") Integer zonaId,
                                 HttpSession session) {
        User user = (User) session.getAttribute("usuarioLogeado");
        Pago pago = new Pago();
        Mesa mesa = new Mesa();
        Zona zona = zonaService.buscarZonaPorID(zonaId)
               .orElseThrow(() -> new RuntimeException("Zona no encontrada"));
        mesa.setId(1);
        
        LocalDateTime fechaActual = LocalDateTime.now();
        SecureRandom random = new SecureRandom();
        String idTransaccion = "PAY-" + (100000 + random.nextInt(900000));
        
        double precio =  0;
        switch (reserva.getZona().getNombre()) {
            case "Muelle Panorámico":
                precio = reserva.getPersonas()*8;
                break;
            case "Mirador Azul":
                precio = reserva.getPersonas()*9;
                break;
            case "Salón Bosque":
                precio = reserva.getPersonas()*10;
                break;
        
            default:
                break;
        }
        reserva.setUser(user);
        reserva.setMesa(mesa);
        reserva.setZona(zona);
        Reserva nuReserva = reservaService.guardarReserva(reserva);
        pago.setMonto(BigDecimal.valueOf(precio));
        pago.setFechaTransaccion(fechaActual);
        pago.setIdTransaccion(idTransaccion);
        pago.setMetodoPago("Tarjeta");
        pago.setReserva(nuReserva);
        pagoService.guardarPago(pago);
        System.out.println("estas en la zona: "+reserva.getZona().getNombre());
        return "index";
    }
    
}
