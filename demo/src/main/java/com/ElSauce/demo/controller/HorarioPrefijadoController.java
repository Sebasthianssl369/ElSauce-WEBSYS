package com.ElSauce.demo.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ElSauce.demo.service.HorarioPrefijadoService;

@RestController
@RequestMapping("/api/horarios")
public class HorarioPrefijadoController {

    private final HorarioPrefijadoService horarioPrefijadoService;

    public HorarioPrefijadoController(HorarioPrefijadoService horarioPrefijadoService) {
        this.horarioPrefijadoService = horarioPrefijadoService;
    }

    @GetMapping
    public List<String> getHorarios() {
        // Este endpoint devolverá una lista de strings con las horas (ej: ["12:00:00", "12:15:00", ...])
        return horarioPrefijadoService.getAllHorasAsString();
    }
}
