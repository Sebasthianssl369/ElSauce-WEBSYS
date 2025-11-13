package com.ElSauce.demo.controller;

import com.ElSauce.demo.model.Reserva;
import com.ElSauce.demo.model.User;
import com.ElSauce.demo.service.HorarioPrefijadoService;
import com.ElSauce.demo.service.MesaService;
import com.ElSauce.demo.service.ReservaService;
import com.ElSauce.demo.service.UserService;
import com.ElSauce.demo.service.ZonaService;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

        // Validar con base de datos
        User admin = userService.login(email, password);

        if (admin != null && admin.getRole().getId() == 1L) {
            // Guardar sesión
            session.setAttribute("adminLogueado", admin);
            redirectAttributes.addFlashAttribute("mensaje", "Bienvenido, administrador " + admin.getNombre());
            return "redirect:/dashboard-menu";
        }else{
            // Si falló el login
            redirectAttributes.addFlashAttribute("error", "Credenciales incorrectas o sin permisos de administrador");
            return "redirect:/admin";
        }

    }

    // Dashboard principal
    @GetMapping("/dashboard-menu")
    public String menu(HttpSession session, Model model) {
        User admin = (User) session.getAttribute("adminLogueado");
        if (admin == null) {
            return "redirect:/admin";
        }
        model.addAttribute("adminLogueado", admin);
        return "dashboard-menu";
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

    // Verifica sesión admin
    User admin = (User) session.getAttribute("adminLogueado");
    if (admin == null) {
        return "redirect:/admin";
    }

    // Datos base
    model.addAttribute("adminLogueado", admin);
    model.addAttribute("reserva", new Reserva());
   model.addAttribute("zonas", zonaService.obtenerTodasLasZonas());
   model.addAttribute("mesas", mesaService.obtenerTodasLasMesas());

    // Lista final de reservas
    List<Reserva> reservasList = reservaService.obtenerTodasLasReservas();

    // --- FILTROS CON PRIORIDAD ---
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

    // 🔹 Siempre enviar la lista al modelo (aunque esté vacía)
    model.addAttribute("reservasList", reservasList);
    model.addAttribute("sinResultados", reservasList.isEmpty());

    return "dashboard-reservas";
}

    @PostMapping("/dashboard-menu")
    public String menuPost(HttpSession session, Model model) {
        return menu(session, model);
    }

    @PostMapping("/dashboard-reservas")
    public String reservasPost(HttpSession session, Model model) {
        return reservas(session, model,null,null,null,null,null);
    }

    @PostMapping("/dashboard-reservas/eliminar")
public String eliminarReserva(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
    try {
        // 1. Buscar la reserva
        Optional<Reserva> reservaOpt = reservaService.buscarPorId(id);

        if (reservaOpt.isPresent()) {
            // 2. Si existe, eliminar
            reservaService.eliminarReserva(reservaOpt.get());
            redirectAttributes.addFlashAttribute("successMessage", "✅ Reserva ID " + id + " eliminada con éxito.");
        } else {
            // 3. Si no existe
            redirectAttributes.addFlashAttribute("errorMessage", "❌ La reserva ID " + id + " no fue encontrada.");
        }
    } catch (Exception e) {
        // 4. Error general
        redirectAttributes.addFlashAttribute("errorMessage", "❌ Error al eliminar la reserva: " + e.getMessage());
    }

    // 5. Redirige al listado principal
    return "redirect:/dashboard-reservas";
}


    // Cierre de sesión
    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin";
    }
}
