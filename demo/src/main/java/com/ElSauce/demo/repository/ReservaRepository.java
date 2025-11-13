package com.ElSauce.demo.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import com.ElSauce.demo.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva,Long>{
    List<Reserva> findByFechaReserva(LocalDate fechaReserva);
    List<Reserva> findByClienteNombreContainingIgnoreCaseOrClienteApellidosContainingIgnoreCase(String nombre, String apellido);
    List<Reserva> findByPersonasGreaterThanEqual(Integer personas);
    List<Reserva> findByHoraReserva(LocalTime horaReserva);
}
