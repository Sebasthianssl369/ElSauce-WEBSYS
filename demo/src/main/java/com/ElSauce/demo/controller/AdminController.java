package com.ElSauce.demo.controller;

import com.ElSauce.demo.Enum.EstadoPago;
import com.ElSauce.demo.Enum.EstadoReserva;
import com.ElSauce.demo.model.Mesa;
import com.ElSauce.demo.model.Pago;
import com.ElSauce.demo.model.Reserva;
import com.ElSauce.demo.model.User;
import com.ElSauce.demo.model.Zona;
import com.ElSauce.demo.service.MesaService;
import com.ElSauce.demo.service.PagoService;
import com.ElSauce.demo.service.ReservaService;
import com.ElSauce.demo.service.UserService;
import com.ElSauce.demo.service.ZonaService;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private ReservaService reservaService; 
    @Autowired
    private ZonaService zonaService; 
    @Autowired
    private MesaService mesaService;
    @Autowired
    private PagoService pagoService;

    // Página inicial del login admin
    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("user", new User());
        return "admin"; // templates/admin.html
    }

    // Login procesado desde formulario
    @PostMapping("/admin")
    public String adminLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User admin = userService.login(email, password);

        if (admin != null && admin.getRole().getId() == 1L) {
            session.setAttribute("adminLogueado", admin);
            redirectAttributes.addFlashAttribute("mensaje", "Bienvenido, administrador " + admin.getNombre());
            return "redirect:/dashboard-reservas";  // ⬅️ AHORA REDIRIGE AQUÍ
        } else {
            redirectAttributes.addFlashAttribute("error", "Credenciales incorrectas o sin permisos de administrador");
            return "redirect:/admin";
        }
    }

    // Página de reservas del panel
    @GetMapping("/dashboard-reservas")
    public String reservas(
            HttpSession session, 
            Model model,
            @RequestParam(value = "filterId", required = false) Long filterId,
            @RequestParam(value = "filterDate", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate filterFecha,
            @RequestParam(value = "filterTime", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime filterHora,
            @RequestParam(value = "filterPeople", required = false) Integer filterPersonas,
            @RequestParam(value = "filterName", required = false) String filterNombre) {

        User admin = (User) session.getAttribute("adminLogueado");
        if (admin == null) {
            return "redirect:/admin";
        }

        model.addAttribute("adminLogueado", admin);
        model.addAttribute("reserva", new Reserva());
        model.addAttribute("zonas", zonaService.obtenerTodasLasZonas());
        model.addAttribute("mesas", mesaService.obtenerTodasLasMesas());

        List<Reserva> reservasList = reservaService.obtenerTodasLasReservas();

        // FILTROS
        if (filterId != null) {
            reservasList = reservasList.stream()
                    .filter(r -> r.getId().equals(filterId))
                    .collect(Collectors.toList());
            model.addAttribute("currentFilterId", filterId);
        }

        if (filterFecha != null) {
            reservasList = reservasList.stream()
                    .filter(r -> filterFecha.equals(r.getFechaReserva()))
                    .collect(Collectors.toList());
            model.addAttribute("currentFilterDate", filterFecha);
        }

        if (filterHora != null) {
            reservasList = reservasList.stream()
                    .filter(r -> filterHora.equals(r.getHoraReserva()))
                    .collect(Collectors.toList());
            model.addAttribute("currentFilterTime", filterHora);
        }

        if (filterPersonas != null) {
            reservasList = reservasList.stream()
                    .filter(r -> r.getPersonas() >= filterPersonas)
                    .collect(Collectors.toList());
            model.addAttribute("currentFilterPeople", filterPersonas);
        }

        if (filterNombre != null && !filterNombre.trim().isEmpty()) {
            String filtro = filterNombre.toLowerCase();
            reservasList = reservasList.stream()
                    .filter(r -> (r.getClienteNombre() + " " + r.getClienteApellidos())
                            .toLowerCase()
                            .contains(filtro))
                    .collect(Collectors.toList());
            model.addAttribute("currentFilterName", filterNombre);
        }

        model.addAttribute("reservasList", reservasList);
        model.addAttribute("sinResultados", reservasList.isEmpty());

        return "dashboard-reservas";
    }

    @PostMapping("/dashboard-reservas")
    public String reservasPost(HttpSession session, Model model) {
        return reservas(session, model, null, null, null, null, null);
    }

    @PostMapping("/dashboard-reservas/eliminar")
    public String eliminarReserva(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Reserva> reservaOpt = reservaService.buscarPorId(id);

            if (reservaOpt.isPresent()) {
                reservaService.eliminarReserva(reservaOpt.get());
                redirectAttributes.addFlashAttribute("successMessage", "✅ Reserva ID " + id + " eliminada con éxito.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ La reserva ID " + id + " no fue encontrada.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Error al eliminar la reserva: " + e.getMessage());
        }

        return "redirect:/dashboard-reservas";
    }

    @PostMapping("/dashboard-reservas/guardar")
    public String guardarReservaAdmin(
            @ModelAttribute Reserva reserva,
            @RequestParam("zona") Integer zonaId,
            @RequestParam("horaReserva") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaReservaStr,
            @RequestParam(value = "numeroTarjeta", required = false) String numeroTarjeta,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User admin = (User) session.getAttribute("adminLogueado");
        if (admin == null) {
            return "redirect:/admin";
        }

        try {
            reserva.setUser(admin);

            Zona zona = zonaService.buscarZonaPorID(zonaId)
                    .orElseThrow(() -> new RuntimeException("Zona no encontrada"));

            reserva.setZona(zona);
            reserva.setHoraReserva(horaReservaStr);
            reserva.setCreatedAt(LocalDateTime.now());

            Optional<Mesa> mesaOpt = reservaService.asignarMesaParaReserva(
                zonaId, 
                reserva.getPersonas(), 
                reserva.getFechaReserva(), 
                horaReservaStr
            );

            if (mesaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ No hay mesas disponibles para esa capacidad en ese horario.");
                return "redirect:/dashboard-reservas";
            }

            reserva.setMesa(mesaOpt.get());

            Reserva reservaGuardada = reservaService.guardarReserva(reserva);

            double precioBase = 7.00; 
            if (zona.getNombre().contains("Muelle")) precioBase = 8.00;
            else if (zona.getNombre().contains("Mirador")) precioBase = 9.00;
            else if (zona.getNombre().contains("Bosque")) precioBase = 10.00;

            BigDecimal monto = BigDecimal.valueOf(reserva.getPersonas() * precioBase);

            Pago pago = new Pago();
            pago.setReserva(reservaGuardada);
            pago.setFechaTransaccion(LocalDateTime.now());
            pago.setMonto(monto);

            if (numeroTarjeta != null && !numeroTarjeta.trim().isEmpty()) {
                pago.setMetodoPago("Tarjeta (Admin)");
                pago.setIdTransaccion("ADM-CARD-" + System.currentTimeMillis());
                pago.setEstadoPago(EstadoPago.PAID);
            } else {
                pago.setMetodoPago("Efectivo");
                pago.setIdTransaccion("ADM-CASH-" + System.currentTimeMillis());
                pago.setEstadoPago(EstadoPago.PENDING); 
            }

            pagoService.guardarPago(pago);

            redirectAttributes.addFlashAttribute("successMessage", "✅ Reserva creada exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Error al guardar: " + e.getMessage());
        }

        return "redirect:/dashboard-reservas";
    }

    // Cierre de sesión
    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin";
    }

    @PutMapping("/admin/reservas/cambiar-estado/{id}")
    @ResponseBody
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long id,
            @RequestParam("estado") EstadoReserva estado) {

        Optional<Reserva> optional = reservaService.buscarPorId(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Reserva reserva = optional.get();
        reserva.setEstado(estado);
        reserva.setUpdatedAt(LocalDateTime.now());

        reservaService.guardarReserva(reserva);

        return ResponseEntity.ok(
            Map.of(
                "success", true,
                "updatedAt", reserva.getUpdatedAt().toString()
            )
        );
    }
}
