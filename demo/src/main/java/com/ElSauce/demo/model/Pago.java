package com.ElSauce.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ElSauce.demo.Enum.EstadoPago;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "pagos")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @Column(name = "payment_token")
    private String paymentToken;

    @Column(name = "card_last4")
    private String cardLast4;

    @Column(name = "card_brand", length = 50)
    private String cardBrand;

    @Column(length = 150)
    private String titular;

    @Column(precision = 8, scale = 2)
    private BigDecimal monto;
    private String moneda = "PEN";

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private EstadoPago paymentStatus = EstadoPago.PENDING;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    //Getter and Setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Reserva getReserva() {
        return reserva;
    }
    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }
    public String getPaymentToken() {
        return paymentToken;
    }
    public void setPaymentToken(String paymentToken) {
        this.paymentToken = paymentToken;
    }
    public String getCardLast4() {
        return cardLast4;
    }
    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
    }
    public String getCardBrand() {
        return cardBrand;
    }
    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }
    public String getTitular() {
        return titular;
    }
    public void setTitular(String titular) {
        this.titular = titular;
    }
    public BigDecimal getMonto() {
        return monto;
    }
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
    public String getMoneda() {
        return moneda;
    }
    public void setMoneda(String moneda) {
        this.moneda = moneda;
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
    public EstadoPago getPaymentStatus() {
        return paymentStatus;
    }
    public void setPaymentStatus(EstadoPago paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
}