package com.example.appalmi.modelos;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alumnos")
public class Alumno {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String cursoNombre;
    private String nombre;
    private String apellido;
    private String rutaFoto;

    public Alumno(String cursoNombre, String nombre, String apellido, String rutaFoto) {
        this.cursoNombre = cursoNombre;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rutaFoto = rutaFoto;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCursoNombre() { return cursoNombre; }
    public void setCursoNombre(String cursoNombre) { this.cursoNombre = cursoNombre; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getRutaFoto() { return rutaFoto; }
    public void setRutaFoto(String rutaFoto) { this.rutaFoto = rutaFoto; }
}