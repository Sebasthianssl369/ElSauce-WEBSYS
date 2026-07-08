package com.ElSauce.demo.dto;

import java.math.BigDecimal;

public class PlatoResponse {

    private Long id;
    private String nombre;
    private BigDecimal precio;
    private String descripcion;
    private String imagenRuta;

    public PlatoResponse() {
    }

    public PlatoResponse(Long id, String nombre, BigDecimal precio,
            String descripcion, String imagenRuta) {

        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.imagenRuta = imagenRuta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagenRuta() {
        return imagenRuta;
    }

    public void setImagenRuta(String imagenRuta) {
        this.imagenRuta = imagenRuta;
    }
}