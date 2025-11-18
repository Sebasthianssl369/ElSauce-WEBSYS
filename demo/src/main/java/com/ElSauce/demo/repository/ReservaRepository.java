package com.ElSauce.demo.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ElSauce.demo.Enum.EstadoReserva;
import com.ElSauce.demo.model.Mesa;
import com.ElSauce.demo.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva,Long>{
    List<Reserva> findByFechaReserva(LocalDate fechaReserva);
    List<Reserva> findByClienteNombreContainingIgnoreCaseOrClienteApellidosContainingIgnoreCase(String nombre, String apellido);
    List<Reserva> findByPersonasGreaterThanEqual(Integer personas);
    List<Reserva> findByHoraReserva(LocalTime horaReserva);
    boolean existsByMesaAndFechaReservaAndHoraReservaAndEstadoIn(Mesa mesa, LocalDate fecha, LocalTime hora, List<EstadoReserva> estados);

    List<LocalTime> findHorasOcupadas(LocalDate fechaReserva, Integer zona, Integer mesa);

}
