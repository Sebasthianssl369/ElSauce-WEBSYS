package com.ElSauce.demo.service;

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
    public void eliminarReserva(Reserva reserva){
        reservaRepository.deleteById(reserva.getId());
    }
}
