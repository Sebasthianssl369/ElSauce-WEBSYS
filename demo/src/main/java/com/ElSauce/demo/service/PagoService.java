package com.ElSauce.demo.service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ElSauce.demo.model.Pago;
import com.ElSauce.demo.model.Reserva;
import com.ElSauce.demo.repository.*;

@Service
public class PagoService {

@Autowired
private PagoRepository pagoRepository;

    public Pago guardarPago(Pago pago){
        return pagoRepository.save(pago);
    }

    public Optional<Pago> buscarPorReserva(Reserva reserva){
        return pagoRepository.findByReserva(reserva);
    }

    public Optional<Pago> buscarPorReservaId(Long reservaId){
        return pagoRepository.findByReservaId(reservaId);
    }
}
