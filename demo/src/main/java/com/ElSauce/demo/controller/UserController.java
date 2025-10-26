package com.ElSauce.demo.controller;


import com.ElSauce.demo.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
            @RequestParam String correo,
            @RequestParam String password,
            @RequestParam String telefono,
            Model model
    ) 
    {
        User user = new User();
        user.setNombre(nombre);
        user.setApellido(apellido);
        user.setEmail(correo);
        user.setPasswordHash(password);
        user.setTelefono(telefono);
        

        try {
            userService.registerUser(user);
            model.addAttribute("mensaje", "Usuario registrado correctamente");
            return "redirect:/login"; // o donde quieras redirigir después
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "index"; // vuelve al modal de registro
        }
    }

    // Login de usuario
    @PostMapping("/login")
    public String login(
            @RequestParam String correo,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        User user = userService.login(correo, password);

        if (user != null && user.getRole().getId() == 2) {
            session.setAttribute("usuario", user);
            return "redirect:/reservas"; // o tu página de reservas
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "index";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
}

