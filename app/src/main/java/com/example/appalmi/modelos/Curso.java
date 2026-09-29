package com.example.appalmi.modelos;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cursos")
public class Curso {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String nombre;
    private String rutaFoto;

    public Curso(String nombre, String rutaFoto) {
        this.nombre = nombre;
        this.rutaFoto = rutaFoto;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRutaFoto() { return rutaFoto; }
    public void setRutaFoto(String rutaFoto) { this.rutaFoto = rutaFoto; }
}