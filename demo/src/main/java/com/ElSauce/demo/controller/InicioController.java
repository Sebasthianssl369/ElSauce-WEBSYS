package com.ElSauce.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class InicioController {

    @GetMapping({"/" , "/index"})
    public String inicio(){
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
    @GetMapping("/reserva")
    public String reserva() {
        return "reserva";
    }
    

   
}
