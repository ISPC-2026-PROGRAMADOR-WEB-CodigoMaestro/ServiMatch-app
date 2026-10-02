package com.ispc.servimatch.model;

public class Ubicacion {

    private int id;
    private String ciudad;
    private String provincia;

    public int getId() {
        return id;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getProvincia() {
        return provincia;
    }

    @Override
    public String toString() {
        return ciudad;
    }
}
