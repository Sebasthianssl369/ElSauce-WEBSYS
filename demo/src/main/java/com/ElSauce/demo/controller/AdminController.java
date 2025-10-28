package com.ElSauce.demo.controller;

import com.ElSauce.demo.model.User;
import com.ElSauce.demo.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Página inicial del login admin
    @GetMapping("/admin")
    public String admin() {
        return "admin"; // templates/admin.html
    }

    // Login procesado desde formulario
    @PostMapping("/dashboard")
    public String dashboard(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Validar con base de datos
        User admin = adminService.login(email, password);

        if (admin != null) {
            // Guardar sesión
            session.setAttribute("admin", admin);
            redirectAttributes.addFlashAttribute("mensaje", "Bienvenido, administrador " + admin.getNombre());
            return "redirect:/dashboard-menu";
        }

        // Si falló el login
        redirectAttributes.addFlashAttribute("error", "Credenciales incorrectas o sin permisos de administrador");
        return "redirect:/admin";
    }

    // Dashboard principal
    @GetMapping("/dashboard-menu")
    public String menu(HttpSession session, Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin";
        }
        model.addAttribute("admin", admin);
        return "dashboard-menu";
    }

    // Página de reservas del panel
    @GetMapping("/dashboard-reservas")
    public String reservas(HttpSession session, Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin";
        }
        model.addAttribute("admin", admin);
        return "dashboard-reservas";
    }

    // Cierre de sesión
    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin";
    }
}
