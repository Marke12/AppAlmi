package com.example.appalmi.modelos;

public class Foto {

    private int imagenResId;
    private String texto;

    public Foto(int imagenResId, String texto) {
        this.imagenResId = imagenResId;
        this.texto = texto;
    }

    public int getImagenResId() {
        return imagenResId;
    }

    public void setImagenResId(int imagenResId) {
        this.imagenResId = imagenResId;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
