package com.ispc.servimatch.model;

public class Oficio {

    private int id;
    private String nombre_oficio;

    private String icono;

    public int getId() {
        return id;
    }

    public String getNombre_oficio() {
        return nombre_oficio;
    }

    public String getIcono() {return icono;}

    @Override
    public String toString() {
        return nombre_oficio;
    }
}