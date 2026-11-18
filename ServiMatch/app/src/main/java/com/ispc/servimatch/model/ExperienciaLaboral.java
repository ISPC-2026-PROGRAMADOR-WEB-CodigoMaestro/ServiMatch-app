package com.ispc.servimatch.model;

public class ExperienciaLaboral {

    private int id;
    private int usuario;
    private String puesto;
    private String empresa;
    private int anio_inicio;
    private Integer anio_fin;
    private String descripcion;

    public ExperienciaLaboral() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuario() {
        return usuario;
    }

    public void setUsuario(int usuario) {
        this.usuario = usuario;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public int getAnio_inicio() {
        return anio_inicio;
    }

    public void setAnio_inicio(int anio_inicio) {
        this.anio_inicio = anio_inicio;
    }

    public Integer getAnio_fin() {
        return anio_fin;
    }

    public void setAnio_fin(Integer anio_fin) {
        this.anio_fin = anio_fin;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}