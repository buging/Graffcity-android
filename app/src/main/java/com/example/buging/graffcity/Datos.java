package com.example.buging.graffcity;

import android.graphics.drawable.Drawable;

/**
 * Created by Buging on 17-01-2016.
 */
public class Datos {
    protected String foto;
    protected String nombre;
    protected String descripcion;
    protected int puntuacion;

    public Datos(String foto, String nombre, String descripcion, int puntuacion){
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.puntuacion = puntuacion;
        this.foto = foto;
    }

    public String getFoto() {
        return foto;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }
}
