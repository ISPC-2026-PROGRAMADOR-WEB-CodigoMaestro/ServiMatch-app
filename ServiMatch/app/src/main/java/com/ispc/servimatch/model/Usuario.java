package com.ispc.servimatch.model;

public class Usuario {

    private int id;
    private String nombre;
    private String email;
    private String contrasena;
    private String dni;
    private Integer rol;
    private Integer oficio;
    private Integer ubicacion;

    // Constructor utilizado para registrar usuarios
    public Usuario(
            String nombre,
            String email,
            String contrasena,
            String dni,
            Integer rol,
            Integer oficio,
            Integer ubicacion
    ) {
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.dni = dni;
        this.rol = rol;
        this.oficio = oficio;
        this.ubicacion = ubicacion;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getDni() {
        return dni;
    }

    public Integer getRol() {
        return rol;
    }

    public Integer getOficio() {
        return oficio;
    }

    public Integer getUbicacion() {
        return ubicacion;
    }
}