package com.ElSauce.demo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.ElSauce.demo.Enum.EstadoReserva;
import com.ElSauce.demo.Enum.TipoDocumento;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "reservas")
public class Reserva {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relaciones
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "mesa_id")
    private Mesa mesa;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "zona_id")
    private Zona zona;

    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Pago pagos;

    // Datos del cliente
    @Column(name = "cliente_nombre")
    private String clienteNombre;

    @Column(name = "cliente_apellidos")
    private String clienteApellidos;

    @Column(name = "cliente_email")
    private String clienteEmail;

    @Column(name = "cliente_telefono")
    private String clienteTelefono;

    @Column(name = "fecha_reserva")
    private LocalDate fechaReserva;

    @Column(name = "hora_reserva")
    private LocalTime horaReserva;

    private Integer personas;

    @Enumerated(EnumType.STRING)
    private TipoDocumento tipo = TipoDocumento.BOLETA;

    @Column(name = "numero_documento", length = 50)
    private String numeroDocumento;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getter y Setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Mesa getMesa() {
        return mesa;
    }
    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }
    public Zona getZona() {
        return zona;
    }
    public void setZona(Zona zona) {
        this.zona = zona;
    }
    public Pago getPagos() {
        return pagos;
    }
    public void setPagos(Pago pagos) {
        this.pagos = pagos;
    }
    public String getClienteNombre() {
        return clienteNombre;
    }
    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }
    public String getClienteApellidos() {
        return clienteApellidos;
    }
    public void setClienteApellidos(String clienteApellidos) {
        this.clienteApellidos = clienteApellidos;
    }
    public String getClienteEmail() {
        return clienteEmail;
    }
    public void setClienteEmail(String clienteEmail) {
        this.clienteEmail = clienteEmail;
    }
    public String getClienteTelefono() {
        return clienteTelefono;
    }
    public void setClienteTelefono(String clienteTelefono) {
        this.clienteTelefono = clienteTelefono;
    }
    public LocalDate getFechaReserva() {
        return fechaReserva;
    }
    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }
    public LocalTime getHoraReserva() {
        return horaReserva;
    }
    public void setHoraReserva(LocalTime horaReserva) {
        this.horaReserva = horaReserva;
    }
    public Integer getPersonas() {
        return personas;
    }
    public void setPersonas(Integer personas) {
        this.personas = personas;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public TipoDocumento getTipo() {
        return tipo;
    }
    public void setTipo(TipoDocumento tipo) {
        this.tipo = tipo;
    }
    public String getNumeroDocumento() {
        return numeroDocumento;
    }
    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }
    public EstadoReserva getEstado() {
        return estado;
    }
    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }
}
