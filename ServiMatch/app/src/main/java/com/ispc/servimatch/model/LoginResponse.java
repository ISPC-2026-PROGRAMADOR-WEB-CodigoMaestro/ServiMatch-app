package com.ispc.servimatch.model;

public class LoginResponse {
    private boolean success;
    private String mensaje;
    private String usuario;

    public boolean isSuccess() {
        return success;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getUsuario() {
        return usuario;
    }
}
