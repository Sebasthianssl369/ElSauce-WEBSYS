package com.ElSauce.demo.controller;


import com.ElSauce.demo.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ElSauce.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Registro de usuario rol 2
    @PostMapping("/register")
    public String register(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String telefono,
            Model model,
            RedirectAttributes redirectAttributes
    ) 
    {
        if (userService.existsByEmail(email)) {
        redirectAttributes.addFlashAttribute("error", "El correo ya está registrado. Intenta con otro.");
        return "redirect:/";
        }

        User user = new User();
        user.setNombre(nombre);
        user.setApellido(apellido);
        user.setEmail(email);
        user.setPasswordHash(password);
        user.setTelefono(telefono);
        

        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario registrado correctamente");
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/index"; // vuelve al modal de registro
        }
        
    }


    // Login de usuario
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        User user = userService.login(email, password);

        if (user != null && user.getRole().getId() == 2) {
            redirectAttributes.addFlashAttribute("mensaje", "Inicio de sesión exitoso. ¡Bienvenido!");
            return "redirect:/"; // o tu página de reservas
        } else {
            redirectAttributes.addFlashAttribute("error", "Correo o contraseña incorrectos.");
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
}

