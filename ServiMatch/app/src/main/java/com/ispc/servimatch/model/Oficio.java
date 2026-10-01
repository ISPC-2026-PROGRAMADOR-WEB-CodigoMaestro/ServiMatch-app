package com.ispc.servimatch.model;

public class Oficio {

    private int id;
    private String nombre_oficio;

    public int getId() {
        return id;
    }

    public String getNombre_oficio() {
        return nombre_oficio;
    }

    @Override
    public String toString() {
        return nombre_oficio;
    }
}