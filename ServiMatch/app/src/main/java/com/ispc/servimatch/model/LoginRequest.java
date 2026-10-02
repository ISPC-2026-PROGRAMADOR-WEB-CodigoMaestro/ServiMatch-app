package com.ispc.servimatch.model;

public class LoginRequest {
    private String email;
    private String contrasena;

    public LoginRequest(String email, String contrasena) {
        this.email = email;
        this.contrasena = contrasena;
    }

    public String getEmail() {
        return email;
    }

    public String getContrasena() {
        return contrasena;
    }
}
