package com.ElSauce.demo.dto;

public class UserResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String rol;

    public UserResponse(
            Long id,
            String nombre,
            String apellido,
            String email,
            String telefono,
            String rol) {

        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getRol() {
        return rol;
    }
}