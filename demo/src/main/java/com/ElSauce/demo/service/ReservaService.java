package com.ElSauce.demo.service;



import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ElSauce.demo.Enum.EstadoReserva;
import com.ElSauce.demo.model.Mesa;
import com.ElSauce.demo.model.Reserva;
import com.ElSauce.demo.model.Zona;
import com.ElSauce.demo.repository.HorarioPrefijadoRepository;
import com.ElSauce.demo.repository.MesaRepository;
import com.ElSauce.demo.repository.ReservaRepository;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private HorarioPrefijadoRepository horarioPrefijadoRepository;

    
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

    public Optional<Mesa> asignarMesaParaReserva(Integer zona, Integer personas, LocalDate fechaReserva, LocalTime hora) {
    List<Mesa> candidatas = mesaRepository.findByZonaIdAndCapacidadGreaterThanEqualOrderByCapacidadAsc(zona, personas);
    for (Mesa m : candidatas) {
        boolean ocupada = reservaRepository.existsByMesaAndFechaReservaAndHoraReservaAndEstadoIn(
            m, fechaReserva, hora, List.of(EstadoReserva.PENDIENTE, EstadoReserva.ASISTIO));
        if (!ocupada) return Optional.of(m);
    }
    return Optional.empty();
    }

    public List<String> obtenerHorariosDisponibles(LocalDate fechaReserva, int zona, int mesa) {
    // horarios totales desde repo (por ejemplo "12:00:00" strings)
    List<String> todosHorarios = horarioPrefijadoRepository.findAllHoras(); // asume List<String> "HH:mm:ss"

    // horarios ocupados desde ReservaRepository --> List<LocalTime>
    List<LocalTime> horariosOcupados(LocalDate fecha, Zona zona, Mesa mesa){
         reservaRepository.findHorasOcupadas(fechaReserva, zona.getId(), mesa.getId());
    }    
    // convertir ocupados a formato "HH:mm:ss" para comparar con todosHorarios
    Set<String> ocupadosStr = horariosOcupados.stream()
        .map(t -> t.toString()) // LocalTime.toString() => "HH:mm[:ss]" (ej "13:30" o "13:30:00")
        .map(s -> {
            // asegura formato "HH:mm:ss"
            if (s.length() == 5) return s + ":00";
            return s;
        })
        .collect(Collectors.toSet());

    return todosHorarios.stream()
            .filter(h -> !ocupadosStr.contains(h))
            .collect(Collectors.toList());
}



}