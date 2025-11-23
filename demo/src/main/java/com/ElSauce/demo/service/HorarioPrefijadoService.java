package com.ElSauce.demo.service;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ElSauce.demo.model.HorarioPrefijado;
import com.ElSauce.demo.repository.HorarioPrefijadoRepository;

@Service
public class HorarioPrefijadoService {
    private final HorarioPrefijadoRepository horarioPrefijadoRepository;

    public HorarioPrefijadoService(HorarioPrefijadoRepository horarioPrefijadoRepository) {
        this.horarioPrefijadoRepository = horarioPrefijadoRepository;
    }

    // Obtiene todas las horas y las convierte a String para el frontend
    public List<String> getAllHorasAsString() {
        return horarioPrefijadoRepository.findAll().stream()
                // Asegúrate de que el formato (HH:MM AM/PM) se haga correctamente si es necesario.
                // Aquí usamos el formato predeterminado de LocalTime, ej: "12:30:00"
                .map(horario -> horario.getHora().toString())
                .collect(Collectors.toList());
    }

   public List<HorarioPrefijado> listarTodos() {
        return horarioPrefijadoRepository.findAll();
    }
}
