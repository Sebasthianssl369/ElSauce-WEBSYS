package com.ElSauce.demo.dto;

public class DashboardResponse {

    private long usuarios;
    private long reservas;
    private long platos;
    private long zonas;

    public DashboardResponse(long usuarios, long reservas, long platos, long zonas) {
        this.usuarios = usuarios;
        this.reservas = reservas;
        this.platos = platos;
        this.zonas = zonas;
    }

    public long getUsuarios() {
        return usuarios;
    }

    public long getReservas() {
        return reservas;
    }

    public long getPlatos() {
        return platos;
    }

    public long getZonas() {
        return zonas;
    }
}
