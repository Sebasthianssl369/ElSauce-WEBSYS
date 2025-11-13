package com.ElSauce.demo.service;



import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ElSauce.demo.model.Reserva;
import com.ElSauce.demo.repository.ReservaRepository;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    
    public Reserva guardarReserva(Reserva reserva){
        return reservaRepository.save(reserva);
    }
    
    public List<Reserva> obtenerTodasLasReservas() {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> buscarReservaPorId(Long id) {
        return reservaRepository.findById(id);
    }
    
    public void eliminarReserva(Reserva reserva){
        reservaRepository.deleteById(reserva.getId());
    }

    //Filtros
    public Optional<Reserva> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty(); 
        }
        return reservaRepository.findById(id);
    }

    public List<Reserva> buscarPorFecha(LocalDate fecha) {
        if (fecha == null) {
            return List.of(); 
        }
        return reservaRepository.findByFechaReserva(fecha);
    }

    public List<Reserva> buscarPorNombreOApellido(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return List.of();
        }
        // Pasamos el mismo término a ambos campos del repositorio
        return reservaRepository.findByClienteNombreContainingIgnoreCaseOrClienteApellidosContainingIgnoreCase(termino, termino);
    }

    public List<Reserva> buscarPorPersonas(Integer personas) {
        if (personas == null || personas <=0) {
            return List.of(); 
        }
        return reservaRepository.findByPersonasGreaterThanEqual(personas);
    }

    public List<Reserva> buscarPorHora(LocalTime horaReserva) {
        if (horaReserva == null) {
            return List.of(); 
        }
        return reservaRepository.findByHoraReserva(horaReserva);
    }

}