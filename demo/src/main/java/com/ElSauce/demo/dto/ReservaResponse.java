package com.ElSauce.demo.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaResponse {

    private Long id;
    private String clienteNombre;
    private String clienteApellidos;
    private String clienteEmail;
    private LocalDate fechaReserva;
    private LocalTime horaReserva;
    private Integer personas;
    private String zona;
    private String mesa;
    private String estado;

    public ReservaResponse(
            Long id,
            String clienteNombre,
            String clienteApellidos,
            String clienteEmail,
            LocalDate fechaReserva,
            LocalTime horaReserva,
            Integer personas,
            String zona,
            String mesa,
            String estado) {

        this.id=id;
        this.clienteNombre=clienteNombre;
        this.clienteApellidos=clienteApellidos;
        this.clienteEmail=clienteEmail;
        this.fechaReserva=fechaReserva;
        this.horaReserva=horaReserva;
        this.personas=personas;
        this.zona=zona;
        this.mesa=mesa;
        this.estado=estado;
    }

    public Long getId(){ return id; }
    public String getClienteNombre(){ return clienteNombre; }
    public String getClienteApellidos(){ return clienteApellidos; }
    public String getClienteEmail(){ return clienteEmail; }
    public LocalDate getFechaReserva(){ return fechaReserva; }
    public LocalTime getHoraReserva(){ return horaReserva; }
    public Integer getPersonas(){ return personas; }
    public String getZona(){ return zona; }
    public String getMesa(){ return mesa; }
    public String getEstado(){ return estado; }
}
