package com.ElSauce.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ElSauce.demo.model.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@Controller
public class InicioController {

    @GetMapping({"/" , "/index"})
    public String inicio(Model model, HttpSession session){
        User usuario = (User) session.getAttribute("usuarioLogeado");
        if (usuario != null) {
            model.addAttribute("usuarioLogeado", usuario);
        }
        return "index";
    }
     @GetMapping({"/nosotros"})
    public String nosotros(){
        return "nosotros";
    }
     @GetMapping({"/galeria"})
    public String galeria(){
        return "galeria";
    }
     @GetMapping({"/eventos"})
    public String eventos(){
        return "eventos";
    }
     @GetMapping({"/noticias"})
    public String noticias(){
        return "noticias";
    }
 
    @GetMapping({"/login"})
    public String login(){
        return "login";
    }
    
    
    @GetMapping({"/registro"})
    public String registro(){
        return "registro";
    }
}
