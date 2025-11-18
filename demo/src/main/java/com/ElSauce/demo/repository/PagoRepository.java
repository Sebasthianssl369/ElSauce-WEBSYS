package com.ElSauce.demo.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ElSauce.demo.model.Pago;
import com.ElSauce.demo.model.Reserva;


public interface PagoRepository extends JpaRepository<Pago,Long>{
    Optional<Pago> findByReserva(Reserva reserva);

    Optional<Pago> findByReservaId(Long reservaId);
}
