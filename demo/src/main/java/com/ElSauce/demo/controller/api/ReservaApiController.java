package com.ElSauce.demo.controller.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ElSauce.demo.Enum.EstadoPago;
import com.ElSauce.demo.Enum.EstadoReserva;
import com.ElSauce.demo.dto.ReservaRequest;
import com.ElSauce.demo.dto.ReservaResponse;
import com.ElSauce.demo.model.Mesa;
import com.ElSauce.demo.model.Pago;
import com.ElSauce.demo.model.Reserva;
import com.ElSauce.demo.model.Zona;
import com.ElSauce.demo.service.PagoService;
import com.ElSauce.demo.service.ReservaService;
import com.ElSauce.demo.service.ZonaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/reservas")
@Tag(name="Reservas", description="API de reservas del restaurante")
public class ReservaApiController {

    private final ReservaService reservaService;
    private final ZonaService zonaService;
    private final PagoService pagoService;

    public ReservaApiController(
            ReservaService reservaService,
            ZonaService zonaService,
            PagoService pagoService) {

        this.reservaService = reservaService;
        this.zonaService = zonaService;
        this.pagoService = pagoService;
    }

    private ReservaResponse convertir(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getClienteNombre(),
                reserva.getClienteApellidos(),
                reserva.getClienteEmail(),
                reserva.getFechaReserva(),
                reserva.getHoraReserva(),
                reserva.getPersonas(),
                reserva.getZona() != null ? reserva.getZona().getNombre() : "",
                reserva.getMesa() != null ? reserva.getMesa().getNumero() : "",
                reserva.getEstado() != null ? reserva.getEstado().name() : ""
        );
    }

    // LISTAR TODAS
    @Operation(summary="Listar reservas")
    @ApiResponses({
        @ApiResponse(responseCode="200", description="Reservas encontradas")
    })
    @GetMapping
    public ResponseEntity<List<ReservaResponse>> listar() {

        List<ReservaResponse> lista = reservaService
                .obtenerTodasLasReservas()
                .stream()
                .map(this::convertir)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    // OBTENER POR ID
    @Operation(summary="Buscar reserva")
    @ApiResponses({
        @ApiResponse(responseCode="200", description="Reserva encontrada"),
        @ApiResponse(responseCode="404", description="No existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {

        Optional<Reserva> reserva = reservaService.buscarPorId(id);

        if (reserva.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Reserva no encontrada");
        }

        return ResponseEntity.ok(convertir(reserva.get()));
    }

    // CREAR RESERVA
    @Operation(summary="Registrar reserva")
    @ApiResponses({
        @ApiResponse(responseCode="201", description="Reserva creada"),
        @ApiResponse(responseCode="400", description="Error de validación")
    })
    @PostMapping
    public ResponseEntity<?> crear(
            @Valid @RequestBody ReservaRequest request) {

        try {

            Zona zona = zonaService
                    .buscarZonaPorID(request.getZonaId())
                    .orElseThrow(() -> new RuntimeException("Zona no encontrada"));

            Optional<Mesa> mesaOpt = reservaService.asignarMesaParaReserva(
                    request.getZonaId(),
                    request.getPersonas(),
                    request.getFechaReserva(),
                    request.getHoraReserva());

            if (mesaOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("No existen mesas disponibles.");
            }

            Reserva reserva = new Reserva();

            reserva.setClienteNombre(request.getClienteNombre());
            reserva.setClienteApellidos(request.getClienteApellidos());
            reserva.setClienteEmail(request.getClienteEmail());
            reserva.setClienteTelefono(request.getClienteTelefono());
            reserva.setFechaReserva(request.getFechaReserva());
            reserva.setHoraReserva(request.getHoraReserva());
            reserva.setPersonas(request.getPersonas());
            reserva.setNumeroDocumento(request.getNumeroDocumento());
            reserva.setZona(zona);
            reserva.setMesa(mesaOpt.get());
            reserva.setEstado(EstadoReserva.PENDIENTE);
            reserva.setCreatedAt(LocalDateTime.now());

            Reserva guardada = reservaService.guardarReserva(reserva);

            double precioBase = 7.0;

            if (zona.getNombre().contains("Muelle")) {
                precioBase = 8.0;
            } else if (zona.getNombre().contains("Mirador")) {
                precioBase = 9.0;
            } else if (zona.getNombre().contains("Bosque")) {
                precioBase = 10.0;
            }

            Pago pago = new Pago();
            pago.setReserva(guardada);
            pago.setMonto(
                    BigDecimal.valueOf(precioBase * request.getPersonas()));
            pago.setFechaTransaccion(LocalDateTime.now());

            if (request.getNumeroTarjeta() != null
                    && !request.getNumeroTarjeta().isBlank()) {
                pago.setMetodoPago("Tarjeta");
                pago.setEstadoPago(EstadoPago.PAID);
                pago.setIdTransaccion("API-" + System.currentTimeMillis());
            } else {
                pago.setMetodoPago("Efectivo");
                pago.setEstadoPago(EstadoPago.PENDING);
                pago.setIdTransaccion("API-CASH-" + System.currentTimeMillis());
            }

            pagoService.guardarPago(pago);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(convertir(guardada));

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // ELIMINAR
    @Operation(summary="Eliminar reserva")
    @ApiResponses({
        @ApiResponse(responseCode="200", description="Reserva eliminada"),
        @ApiResponse(responseCode="404", description="Reserva no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {

        Optional<Reserva> reserva = reservaService.buscarPorId(id);

        if (reserva.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Reserva no encontrada");
        }

        reservaService.eliminarReserva(reserva.get());

        return ResponseEntity.ok("Reserva eliminada correctamente");
    }

}
