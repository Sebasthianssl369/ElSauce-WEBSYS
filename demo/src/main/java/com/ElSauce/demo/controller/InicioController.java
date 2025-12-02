package com.ElSauce.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ElSauce.demo.model.User;
import com.ElSauce.demo.repository.PlatoRepository;

import jakarta.servlet.http.HttpSession;






@Controller
public class InicioController {
   
     private final PlatoRepository platoRepository;

    // INYECTAMOS EL REPOSITORY AQUÍ
    public InicioController(PlatoRepository platoRepository) {
        this.platoRepository = platoRepository;
    }

    @GetMapping({"/" , "/index"})
    public String inicio(Model model, HttpSession session){
        User usuario = (User) session.getAttribute("usuarioLogeado");
        if (usuario != null) {
            model.addAttribute("usuarioLogeado", usuario);
        }

        model.addAttribute("platos", platoRepository.findAll());
        System.out.println("Cargando platos: " + platoRepository.count());
        
        return "index";
    }
     @GetMapping({"/nosotros"})
    public String nosotros(Model model, HttpSession session) {
    User usuario = (User) session.getAttribute("usuarioLogeado");
    model.addAttribute("usuarioLogeado", usuario); 
        return "nosotros";
    }
     @GetMapping({"/galeria"})
    public String galeria(Model model, HttpSession session) {
    User usuario = (User) session.getAttribute("usuarioLogeado");
    model.addAttribute("usuarioLogeado", usuario); 
        return "galeria";
    }
     @GetMapping({"/eventos"})
    public String eventos(Model model, HttpSession session) {
    User usuario = (User) session.getAttribute("usuarioLogeado");
    model.addAttribute("usuarioLogeado", usuario); 
        return "eventos";
    }
     @GetMapping({"/noticias"})
    public String noticias(Model model, HttpSession session) {
    User usuario = (User) session.getAttribute("usuarioLogeado");
    model.addAttribute("usuarioLogeado", usuario); 
        return "noticias";
    }
 
    @GetMapping({"/login"})
    public String login(Model model, HttpSession session) {
    User usuario = (User) session.getAttribute("usuarioLogeado");
    model.addAttribute("usuarioLogeado", usuario); 
        return "login";
    }
    
    
    @GetMapping({"/registro"})
    public String registro(Model model, HttpSession session) {
    User usuario = (User) session.getAttribute("usuarioLogeado");
    model.addAttribute("usuarioLogeado", usuario); 
        return "registro";
    }

    
}
