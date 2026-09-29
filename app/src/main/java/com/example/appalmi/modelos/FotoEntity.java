package com.example.appalmi.modelos;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "fotos")
public class FotoEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String ruta;
    private String titulo;
    private String ubicacion; // "interior" o "exterior"

    public FotoEntity(String ruta, String titulo, String ubicacion) {
        this.ruta = ruta;
        this.titulo = titulo;
        this.ubicacion = ubicacion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRuta() { return ruta; }
    public void setRuta(String ruta) { this.ruta = ruta; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
}