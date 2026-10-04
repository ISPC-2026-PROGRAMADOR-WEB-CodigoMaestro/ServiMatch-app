package com.ispc.servimatch.model;

public class LoginResponse {

    private String mensaje;
    private int id;
    private String nombre;
    private int id_rol;
    private String rol;

    public String getMensaje() {
        return mensaje;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getId_rol() {
        return id_rol;
    }

    public String getRol() {
        return rol;
    }
}