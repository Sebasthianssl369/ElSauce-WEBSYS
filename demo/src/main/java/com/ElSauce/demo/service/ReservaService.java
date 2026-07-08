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
    
    public List<Reserva> obtenerTodasLasReservas() 
    {
        return reservaRepository.findAll();
    }

    // --- MÉTODOS NUEVOS AGREGADOS AQUÍ ---

    // Obtener una reserva por ID
    public Optional<Reserva> obtenerPorId(Long id) {
        return reservaRepository.findById(id);
    }

    // Actualizar una reserva
    public Reserva actualizar(Long id, Reserva reserva) {
        Reserva existente = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        existente.setClienteNombre(reserva.getClienteNombre());
        existente.setClienteApellidos(reserva.getClienteApellidos());
        existente.setClienteEmail(reserva.getClienteEmail());
        existente.setClienteTelefono(reserva.getClienteTelefono());

        existente.setFechaReserva(reserva.getFechaReserva());
        existente.setHoraReserva(reserva.getHoraReserva());
        existente.setPersonas(reserva.getPersonas());

        existente.setZona(reserva.getZona());
        existente.setMesa(reserva.getMesa());

        existente.setEstado(reserva.getEstado());
        existente.setTipo(reserva.getTipo());
        existente.setNumeroDocumento(reserva.getNumeroDocumento());

        return reservaRepository.save(existente);
    }

    // Eliminar por ID
    public void eliminar(Long id) {
        reservaRepository.deleteById(id);
    }

    // --- FIN DE LOS MÉTODOS NUEVOS ---

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

        // 1. Horarios totales como LocalTime
        List<LocalTime> todosHorarios = horarioPrefijadoRepository.findAllHoras();

        // 2. Horarios ocupados como LocalTime
        List<LocalTime> ocupados = reservaRepository.findHorasOcupadas(fecha, zonaId, mesaId);

        // 3. Convertir los ocupados a String "HH:mm:ss"
        Set<String> ocupadosStr = ocupados.stream()
                .map(t -> t.toString().length() == 5 ? t + ":00" : t.toString())
                .collect(Collectors.toSet());

        // 4. Convertir todos los horarios a String "HH:mm:ss"
        List<String> todosStr = todosHorarios.stream()
                .map(t -> t.toString().length() == 5 ? t + ":00" : t.toString())
                .toList();

        // 5. Filtrar disponibles (String vs String)
        return todosStr.stream()
                .filter(h -> !ocupadosStr.contains(h))
                .toList();
    }

    public List<Reserva> buscarPorUsuarioYFechaMayorOIgual(Long userId, LocalDate fecha) {
        return reservaRepository.findByUser_IdAndFechaReservaGreaterThanEqualOrderByFechaReservaAsc(userId, fecha);
    }

    public List<Reserva> buscarPorUsuarioYFechaMenor(Long userId, LocalDate fecha) {
        return reservaRepository.findByUser_IdAndFechaReservaLessThanOrderByFechaReservaDesc(userId, fecha);
    }

}
