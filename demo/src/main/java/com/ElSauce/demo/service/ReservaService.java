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
import com.ElSauce.demo.repository.ZonaRepository;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private HorarioPrefijadoRepository horarioPrefijadoRepository;

    @Autowired
    private ZonaRepository zonaRepository;
    
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

  
    public List<String> obtenerHorariosDisponibles(LocalDate fecha, Integer zonaId, Integer mesaId) {

    // 1. Horarios totales como STRING (12:00:00 ...)
    List<String> todosHorarios = horarioPrefijadoRepository.findAllHoras();

    // 2. Horarios ocupados desde la BD (LocalTime)
    List<LocalTime> ocupados = reservaRepository.findHorasOcupadas(fecha, zonaId, mesaId);

    // 3. Convertir LocalTime → String "HH:mm:ss"
    Set<String> ocupadosStr = ocupados.stream()
            .map(t -> t.toString().length() == 5 ? t + ":00" : t.toString())
            .collect(Collectors.toSet());

    // 4. Filtrar los disponibles
    return todosHorarios.stream()
            .filter(h -> !ocupadosStr.contains(h))
            .toList();
}




}