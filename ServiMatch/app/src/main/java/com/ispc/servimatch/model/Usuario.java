package com.ispc.servimatch.model;

public class Usuario {

    private String nombre;
    private String email;
    private String contrasena;
    private String dni;
    private Integer rol;
    private Integer oficio;
    private Integer ubicacion;

    public Usuario(String nombre, String email, String contrasena, String dni,
                   Integer rol, Integer oficio, Integer ubicacion) {
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.dni = dni;
        this.rol = rol;
        this.oficio = oficio;
        this.ubicacion = ubicacion;
    }
}